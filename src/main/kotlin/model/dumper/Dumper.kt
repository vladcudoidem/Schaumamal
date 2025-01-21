package model.dumper

import com.android.adblib.AdbSession
import com.android.adblib.ConnectedDevice
import com.android.adblib.ConnectedDevicesTracker
import com.android.adblib.ShellCommandOutput
import com.android.adblib.connectedDevicesTracker
import com.android.adblib.fileSystem
import com.android.adblib.rootAndWait
import com.android.adblib.shell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import model.hash
import model.platform.PlatformInformationProvider
import model.repository.Content
import model.repository.Display
import model.repository.Dump
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteRecursively
import kotlin.io.path.readText
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class Dumper(
    platformInformationProvider: PlatformInformationProvider,
    private val adbSession: AdbSession,
    private val nicknameProvider: NicknameProvider
) {
    private val shortTimeout = 4.seconds
    private val dumpTimeout = 20.seconds

    private val remoteDumpFilePath = "/sdcard/dump.xml"
    private fun remoteScreenshotFilePath(name: String) = "/sdcard/$name"

    private val appDirectoryPath = Path(platformInformationProvider.getAppDirectoryPath())

    init {
        appDirectoryPath.createDirectories()
    }

    @OptIn(ExperimentalPathApi::class)
    fun dump(content: Content): DumpResult = runBlocking(timeout = dumpTimeout) {
        // Todo: should I always pass Content and Settings or just the necessary fields?

        // Todo: make sure that all command exit codes are handled

        // Todo: use custom exceptions? Or no exceptions at all and objects instead?

        // Todo:
        //  - check that system health is retained if dump process is interrupted at any point
        //  - split up this method into multiple methods

        // Record the exact time of the dump
        val timeMilliseconds = System.currentTimeMillis()

        val lastNickname = content.dumps.first().nickname
        val nextNickname = nicknameProvider.getNext(current = lastNickname)

        // Dynamically retrieve any device
        val device = withTimeoutOrNull(shortTimeout) {
            adbSession.connectedDevicesTracker.waitForAnyDevice()
        } ?: return@runBlocking DumpResult.Error("No device connected.")

        // Attempt to root device
        withTimeoutOrNull(shortTimeout) {
            device.rootAndWait()
        } ?: return@runBlocking DumpResult.Error("Root process took too long.")

        val deviceShell = device.shell
        val deviceFileSystem = device.fileSystem

        // Make sure that the temporary directory exists and clear its contents
        val tempDirectoryPath = appDirectoryPath.resolve(content.tempDirectoryName)
        tempDirectoryPath.deleteRecursively()
        tempDirectoryPath.createDirectories()

        // Dump the UI
        deviceShell.executeAsText(
            commandTimeout = shortTimeout.toJavaDuration(),
            command = "uiautomator dump --windows $remoteDumpFilePath"
        ).ifNonZeroExit { return@runBlocking DumpResult.Error("Root process took too long.") }

        // Pull the dump file from the device
        val dumpFileName = "dump_${hash()}"
        try {
            deviceFileSystem.receiveFile(
                remoteFilePath = remoteDumpFilePath,
                destinationPath = tempDirectoryPath.resolve(dumpFileName)
            )
        } catch (e: Exception) {
            return@runBlocking DumpResult.Error("Could not pull dump file from device.")
        }

        // Remove the dump file from the device
        deviceShell.executeAsText(
            commandTimeout = shortTimeout.toJavaDuration(),
            command = "rm $remoteDumpFilePath"
        )

        val api =
            deviceShell.executeAsText(
                commandTimeout = shortTimeout.toJavaDuration(),
                command = "getprop ro.build.version.sdk"
            )
                .ifNonZeroExit { return@runBlocking DumpResult.Error("Could not retrieve device API level.") }
                .stdout
                .toInt()

        val flingerOutput =
            deviceShell.executeAsText(
                commandTimeout = shortTimeout.toJavaDuration(),
                command = "dumpsys SurfaceFlinger --displays"
            )
                .ifNonZeroExit {
                    return@runBlocking DumpResult.Error(
                        "Could not retrieve display IDs (SurfaceFlinger failed)."
                    )
                }
                .stdout

        val getDisplaysOutput =
            deviceShell.executeAsText(
                commandTimeout = shortTimeout.toJavaDuration(),
                command = "cmd display get-displays"
            )
                .ifNonZeroExit {
                    return@runBlocking DumpResult.Error(
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
        )

        val displays = mutableListOf<Display>()
        for (resolvedDisplay in resolvedDisplays) {
            if (resolvedDisplay.screenshotId == null) continue

            val screenshotFileName = "scr_${hash()}.png"
            val remoteScreenshotFilePath = remoteScreenshotFilePath(screenshotFileName)

            val screenshotOutput =
                deviceShell.executeAsText(
                    commandTimeout = shortTimeout.toJavaDuration(),
                    command = "screencap -d ${resolvedDisplay.screenshotId} $remoteScreenshotFilePath"
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
            deviceShell.executeAsText(
                commandTimeout = shortTimeout.toJavaDuration(),
                command = "rm $remoteScreenshotFilePath"
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

        // return
        DumpResult.Success(dump)
    } ?: error("Dump took too long. Aborting.")

    @Suppress("DuplicatedCode")
    private fun resolveDisplays(
        api: Int,
        flingerOutput: String,
        getDisplaysOutput: String,
        dumpOutput: String
    ): List<ResolvedDisplay> {

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
        val resolvedDisplays: List<ResolvedDisplay> = when (api) {

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
                val resolveMultipleVirtualDisplays = false // Todo: add option to select the algorithm

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

            else -> error("Devices with API $api is not supported.")
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

fun <T> runBlocking(
    timeout: Duration,
    block: suspend CoroutineScope.() -> T
): T? =
    runBlocking {
        withTimeoutOrNull(
            timeout = timeout,
            block = block
        )
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