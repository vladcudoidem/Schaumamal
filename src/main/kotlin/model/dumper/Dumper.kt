package model.dumper

import com.android.adblib.AdbSession
import com.android.adblib.AdbSessionHost
import com.android.adblib.ConnectedDevice
import com.android.adblib.ConnectedDevicesTracker
import com.android.adblib.ShellCommandOutput
import com.android.adblib.ShellManager
import com.android.adblib.connectedDevicesTracker
import com.android.adblib.fileSystem
import com.android.adblib.rootAndWait
import com.android.adblib.shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import model.dumper.dataClasses.CmdDisplay
import model.dumper.dataClasses.DumpDisplay
import model.dumper.dataClasses.FlingerDisplay
import model.dumper.dataClasses.ResolvedDisplay
import model.platform.PlatformInformationProvider
import model.repository.dataClasses.Display
import model.repository.dataClasses.Dump
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteRecursively
import kotlin.io.path.readText
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Dumper(
    platformInformationProvider: PlatformInformationProvider,
    private val adbSessionHost: AdbSessionHost,
    private val nicknameProvider: NicknameProvider
) {
    private val shortTimeout = 8.seconds
    private val dumpTimeout = 40.seconds

    private val remoteDumpFilePath = "/sdcard/dump.xml"
    private fun remoteScreenshotFilePath(name: String) = "/sdcard/$name"

    private val appDirectoryPath = Path(platformInformationProvider.getAppDirectoryPath())

    @OptIn(ExperimentalPathApi::class)
    suspend fun dump(
        lastNickname: String?,
        tempDirectoryName: String
    ): DumpResult =
        withContext(Dispatchers.IO) {
            withTimeoutOrNull(dumpTimeout) {
                // First establish ADB connection.
                val adbSession = withTimeoutOrNull(shortTimeout) {
                    AdbSession.create(adbSessionHost)
                } ?: return@withTimeoutOrNull DumpResult.Error("Could not establish ADB connection.")

                // Todo:
                //  - check that system health is retained if dump process is interrupted at any point
                //  - split up this method into multiple methods

                // Record the exact time of the dump
                val timeMilliseconds = System.currentTimeMillis()

                val nextNickname = nicknameProvider.getNext(current = lastNickname)

                // Dynamically retrieve any device
                val device = withTimeoutOrNull(shortTimeout) {
                    adbSession.connectedDevicesTracker.waitForAnyDevice()
                } ?: return@withTimeoutOrNull DumpResult.Error("No device connected.")

                // Attempt to root device
                withTimeoutOrNull(shortTimeout) {
                    device.rootAndWait()
                } ?: return@withTimeoutOrNull DumpResult.Error("Root process took too long.")

                val deviceShell = device.shell
                val deviceFileSystem = device.fileSystem

                // Make sure that the temporary directory exists and clear its contents
                val tempDirectoryPath = appDirectoryPath.resolve(tempDirectoryName)
                tempDirectoryPath.deleteRecursively()
                tempDirectoryPath.createDirectories()

                // Dump the UI
                deviceShell.executeWithTimeout(
                    command = "uiautomator dump --windows $remoteDumpFilePath",
                    commandTimeout = shortTimeout,
                    timeoutAction = {
                        return@withTimeoutOrNull DumpResult.Error("XML Dump process took too long.")
                    }
                )
                    .ifNonZeroExit { return@withTimeoutOrNull DumpResult.Error("XML Dump failed.") }

                // Pull the dump file from the device
                val dumpFileName = "dump_${hash()}.xml"
                try {
                    deviceFileSystem.receiveFile(
                        remoteFilePath = remoteDumpFilePath,
                        destinationPath = tempDirectoryPath.resolve(dumpFileName)
                    )
                } catch (e: Exception) {
                    return@withTimeoutOrNull DumpResult.Error("Could not pull dump file from device.")
                }

                // Remove the dump file from the device
                deviceShell.executeWithTimeout(
                    command = "rm $remoteDumpFilePath",
                    commandTimeout = shortTimeout,
                    timeoutAction = {
                        return@withTimeoutOrNull DumpResult.Error(
                            "Removing the dump file from device took too long."
                        )
                    }
                )

                val api =
                    deviceShell.executeWithTimeout(
                        command = "getprop ro.build.version.sdk",
                        commandTimeout = shortTimeout,
                        timeoutAction = {
                            return@withTimeoutOrNull DumpResult.Error(
                                "Getting the device API level took too long."
                            )
                        }
                    )
                        .ifNonZeroExit {
                            return@withTimeoutOrNull DumpResult.Error("Could not retrieve device API level.")
                        }
                        .stdout
                        .trim()
                        .toInt()

                val flingerOutput =
                    deviceShell.executeWithTimeout(
                        command = "dumpsys SurfaceFlinger --displays",
                        commandTimeout = shortTimeout,
                        timeoutAction = {
                            return@withTimeoutOrNull DumpResult.Error(
                                "Getting the display IDs (SurfaceFlinger) took too long."
                            )
                        }
                    )
                        .ifNonZeroExit {
                            return@withTimeoutOrNull DumpResult.Error(
                                "Could not retrieve display IDs (SurfaceFlinger failed)."
                            )
                        }
                        .stdout

                val getDisplaysOutput =
                    deviceShell.executeWithTimeout(
                        command = "cmd display get-displays",
                        commandTimeout = shortTimeout,
                        timeoutAction = {
                            return@withTimeoutOrNull DumpResult.Error(
                                "Getting the display IDs (get-displays) took too long."
                            )
                        }
                    )
                        .ifNonZeroExit {
                            return@withTimeoutOrNull DumpResult.Error(
                                "Could not retrieve display IDs (get-displays failed)."
                            )
                        }
                        .stdout

                val dumpOutput = tempDirectoryPath.resolve(dumpFileName).readText()

                // Contains the connections between the dump IDs and the screenshot IDs.
                val resolvedDisplays = resolveDisplays(
                    api = api,
                    flingerOutput = flingerOutput,
                    getDisplaysOutput = getDisplaysOutput,
                    dumpOutput = dumpOutput
                ) ?: return@withTimeoutOrNull DumpResult.Error("Devices with API $api are not supported.")

                val displays = mutableListOf<Display>()
                for (resolvedDisplay in resolvedDisplays) {
                    if (resolvedDisplay.screenshotId == null) continue

                    val screenshotFileName = "scr_${hash()}.png"
                    val remoteScreenshotFilePath = remoteScreenshotFilePath(screenshotFileName)

                    val screenshotOutput =
                        deviceShell.executeWithTimeout(
                            command = "screencap -d ${resolvedDisplay.screenshotId} $remoteScreenshotFilePath",
                            commandTimeout = shortTimeout,
                            timeoutAction = {
                                return@withTimeoutOrNull DumpResult.Error(
                                    "Taking a screenshot (id ${resolvedDisplay.screenshotId}) took too long."
                                )
                            }
                        )
                    // Continue if the screenshot taking process was problematic (e.g. invalid ID).
                    if (screenshotOutput.exitCode != 0) continue

                    // Todo: look into what is needed for exitCode to work as expected

                    try {
                        // Pull screenshot file from device
                        deviceFileSystem.receiveFile(
                            remoteFilePath = remoteScreenshotFilePath,
                            destinationPath = tempDirectoryPath.resolve(screenshotFileName)
                        )
                    } catch (e: Exception) {
                        continue
                    }

                    // Remove the screenshot file from the device
                    deviceShell.executeWithTimeout(
                        command = "rm $remoteScreenshotFilePath",
                        commandTimeout = shortTimeout,
                        timeoutAction = {
                            return@withTimeoutOrNull DumpResult.Error(
                                "Removing the screenshot file (id ${resolvedDisplay.screenshotId}) from " +
                                        "device took too long."
                            )
                        }
                    )

                    displays.add(
                        Display(
                            id = resolvedDisplay.dumpId,
                            screenshotFileName = screenshotFileName
                        )
                    )
                }

                val dump = Dump(
                    directoryName = "",
                    nickname = nextNickname,
                    timeMilliseconds = timeMilliseconds,
                    xmlTreeFileName = dumpFileName,
                    displays = displays
                )

                return@withTimeoutOrNull DumpResult.Success(dump)
            }
        } ?: DumpResult.Error("Dump process took too long (more than $dumpTimeout).")

    @Suppress("DuplicatedCode")
    private fun resolveDisplays(
        api: Int,
        flingerOutput: String,
        getDisplaysOutput: String,
        dumpOutput: String
    ): List<ResolvedDisplay>? {

        // Todo: maybe use classes to offer different implementations

        val dumpDisplays =
            dumpOutput.extractAll(
                pattern = """<display id="(\d+)">""".toRegex(RegexOption.DOT_MATCHES_ALL)
            ) {
                DumpDisplay(
                    dumpId = it.component1()
                )
            }

        // This when statement partly contains duplicated code.
        val resolvedDisplays: List<ResolvedDisplay>? = when (api) {

            35 -> {
                val flingerDisplays =
                    flingerOutput.extractAll(
                        pattern = """(\w*)\s?Display (\d+)\s""".toRegex(RegexOption.DOT_MATCHES_ALL)
                    ) {
                        FlingerDisplay(
                            isVirtual = it.component1().equals("Virtual", ignoreCase = true),
                            screenshotId = it.component2()
                        )
                    }

                val cmdDisplays =
                    getDisplaysOutput.extractAll(
                        pattern = """Display id (\d+).*?type (\w+).*?uniqueId ".*?:(\d+)""""
                            .toRegex(RegexOption.DOT_MATCHES_ALL)
                    ) {
                        CmdDisplay(
                            dumpId = it.component1(),
                            isVirtual = it.component2().equals("VIRTUAL", ignoreCase = true),
                            screenshotId = it.component3()
                        )
                    }

                // Whether to resolve an equal number of virtual displays in flingerDisplays and cmdDisplays by assuming
                // that they are listed in the same order. If this is false, virtual displays are resolved only if there
                // is exactly one in flingerDisplays and one in cmdDisplays.
                val resolveMultipleVirtualDisplays = true // Todo: add option to select the algorithm

                val flingerVirtuals = flingerDisplays.filter { it.isVirtual }
                val cmdVirtuals = cmdDisplays.filter { it.isVirtual }
                val virtualDumpToScreenshotMap: Map<String, String> =
                    if (resolveMultipleVirtualDisplays) {
                        if (flingerVirtuals.size == cmdVirtuals.size) {
                            (cmdVirtuals.map { it.dumpId }) zipMap (flingerVirtuals.map { it.screenshotId })
                        } else {
                            emptyMap()
                        }
                    } else {
                        if (flingerVirtuals.size == 1 && cmdVirtuals.size == 1) {
                            (cmdVirtuals.map { it.dumpId }) zipMap (flingerVirtuals.map { it.screenshotId })
                        } else {
                            emptyMap()
                        }
                    }

                val nonVirtualCmdDisplays = cmdDisplays.filter { !it.isVirtual }
                val dumpToScreenshotMap =
                    nonVirtualCmdDisplays.associate { it.dumpId to it.screenshotId } + virtualDumpToScreenshotMap

                // return
                dumpDisplays.map {
                    ResolvedDisplay(
                        screenshotId = dumpToScreenshotMap[it.dumpId],
                        dumpId = it.dumpId
                    )
                }
            }

            34, 33 -> {
                val cmdDisplays =
                    getDisplaysOutput.extractAll(
                        pattern = """Display id (\d+).*?type (\w+).*?uniqueId ".*?:(\d+)""""
                            .toRegex(RegexOption.DOT_MATCHES_ALL)
                    ) {
                        CmdDisplay(
                            dumpId = it.component1(),
                            isVirtual = it.component2().equals("VIRTUAL", ignoreCase = true),
                            screenshotId = it.component3()
                        )
                    }

                val nonVirtualCmdDisplays = cmdDisplays.filter { !it.isVirtual }
                val dumpToScreenshotMap = nonVirtualCmdDisplays.associate { it.dumpId to it.screenshotId }

                // return
                dumpDisplays.map {
                    ResolvedDisplay(
                        screenshotId = dumpToScreenshotMap[it.dumpId],
                        dumpId = it.dumpId
                    )
                }
            }

            32, 31 -> {
                // return
                dumpDisplays.map {
                    ResolvedDisplay(
                        screenshotId = it.dumpId,
                        dumpId = it.dumpId
                    )
                }
            }

            else -> null
        }

        return resolvedDisplays
    }
}

// Todo: move these to separate file

suspend fun ConnectedDevicesTracker.waitForAnyDevice(): ConnectedDevice {
    // Do a quick scan on the current state first (more efficient), then wait on the StateFlow.
    return connectedDevices.value.firstOrNull() ?: run {
        connectedDevices.transform { devices ->
            emit(devices.firstOrNull())
        }.filterNotNull().first()
    }
}

fun <T> String.extractAll(
    pattern: Regex,
    transform: (MatchResult.Destructured) -> T
): List<T> {
    val results = pattern.findAll(this)
    return results.toList().map { it.destructured }.map(transform)
}

infix fun <T, R> Iterable<T>.zipMap(other: Iterable<R>): Map<T, R> =
    zip(other).toMap()

inline fun ShellCommandOutput.ifNonZeroExit(action: () -> Unit) =
    apply { if (exitCode != 0) action() }

suspend inline fun ShellManager.executeWithTimeout(
    command: String,
    commandTimeout: Duration,
    timeoutAction: () -> Nothing
): ShellCommandOutput =
    withTimeoutOrNull(commandTimeout) {
        executeAsText(command)
    } ?: timeoutAction()