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
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteRecursively
import kotlin.io.path.readText
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import model.dumper.dataClasses.CmdDisplay
import model.dumper.dataClasses.DumpDisplay
import model.dumper.dataClasses.FlingerDisplay
import model.dumper.dataClasses.ResolvedDisplay
import model.platform.PlatformInformationProvider
import model.repository.dataClasses.Display
import model.repository.dataClasses.Dump
import model.utils.copyToClipboard
import viewmodel.notification.Notification
import viewmodel.notification.NotificationAction
import viewmodel.notification.NotificationSeverity
import viewmodel.notification.timeout

class Dumper(
    platformInformationProvider: PlatformInformationProvider,
    private val adbSessionHost: AdbSessionHost,
    private val nicknameProvider: NicknameProvider,
) {
    private val shortTimeout = 12.seconds
    private val dumpTimeout = 40.seconds

    private val remoteDumpFilePath = "/sdcard/dump.xml"

    private fun remoteScreenshotFilePath(name: String) = "/sdcard/$name"

    private val appDirectoryPath = Path(platformInformationProvider.getAppDirectoryPath())

    @OptIn(ExperimentalPathApi::class)
    suspend fun dump(
        lastNickname: String?,
        tempDirectoryName: String,
        dumpProgressHandler: DumpProgressHandler,
    ): DumpResult =
        withContext(Dispatchers.IO) {
            withTimeoutOrNull(dumpTimeout) {
                dumpProgressHandler.reportStartingDump()

                // First establish ADB connection.
                val adbSession =
                    withTimeoutOrNull(shortTimeout) { AdbSession.create(adbSessionHost) }
                        ?: return@withTimeoutOrNull DumpResult.Error(
                            Notification(
                                title = "ADB Session Error",
                                description =
                                    "Could not establish ADB connection. Please check that ADB is installed and that the usual commands work.",
                                severity = NotificationSeverity.ERROR,
                                exitStrategy = timeout(5.seconds),
                            )
                        )

                // Todo:
                //  - check that system health is retained if dump process is interrupted at any
                // point
                //  - split up this method into multiple methods

                // Record the exact time of the dump
                val timeMilliseconds = System.currentTimeMillis()

                val nextNickname = nicknameProvider.getNext(current = lastNickname)

                // Dynamically retrieve any device
                val device =
                    withTimeoutOrNull(shortTimeout) {
                        adbSession.connectedDevicesTracker.waitForAnyDevice()
                    }
                        ?: return@withTimeoutOrNull DumpResult.Error(
                            Notification(
                                title = "No Device Connected",
                                description =
                                    "Cannot find a device that is reachable through ADB. Connect to a device or start an emulator.",
                                severity = NotificationSeverity.ERROR,
                                exitStrategy = timeout(8.seconds),
                            )
                        )

                // Attempt to root device
                withTimeoutOrNull(shortTimeout) { device.rootAndWait() }
                    ?: return@withTimeoutOrNull DumpResult.Error(
                        Notification(
                            title = "ADB Root Timeout",
                            description = "ADB root command took too long. Try again.",
                            severity = NotificationSeverity.ERROR,
                            exitStrategy = timeout(5.seconds),
                        )
                    )

                val deviceShell = device.shell
                val deviceFileSystem = device.fileSystem

                // Make sure that the temporary directory exists and clear its contents
                val tempDirectoryPath = appDirectoryPath.resolve(tempDirectoryName)
                tempDirectoryPath.deleteRecursively()
                tempDirectoryPath.createDirectories()

                dumpProgressHandler.reportPreDumpSetupFinished()

                val dumpCommandForClipboard = "adb shell uiautomator dump"
                val adbKillStartCommandForClipboard = "adb kill-server && adb start-server"

                // Dump the UI
                deviceShell
                    .executeWithTimeout(
                        command = "uiautomator dump --windows $remoteDumpFilePath",
                        commandTimeout = shortTimeout,
                        timeoutAction = {
                            return@withTimeoutOrNull DumpResult.Error(
                                Notification(
                                    title = "XML Dump Timeout",
                                    description =
                                        "The XML dump ran into a timeout. This might be caused by the UI not reaching idle state, in which case it cannot be dumped. To test this, try executing \"$dumpCommandForClipboard\". Or maybe just try again.",
                                    severity = NotificationSeverity.ERROR,
                                    actions =
                                        listOf(
                                            NotificationAction(
                                                title = "Copy \"$dumpCommandForClipboard\"",
                                                block = { copyToClipboard(dumpCommandForClipboard) },
                                            )
                                        ),
                                )
                            )
                        },
                    )
                    .ifNonZeroExit {
                        return@withTimeoutOrNull DumpResult.Error(
                            Notification(
                                title = "XML Dump Failed",
                                description =
                                    "Try again. Or try executing \"$dumpCommandForClipboard\" to see why the dump is not working. Sometimes it helps to (1) restart the emulator or (2) execute \"$adbKillStartCommandForClipboard\".",
                                severity = NotificationSeverity.ERROR,
                                actions =
                                    listOf(
                                        NotificationAction(
                                            title = "Copy \"$dumpCommandForClipboard\"",
                                            block = { copyToClipboard(dumpCommandForClipboard) },
                                        ),
                                        NotificationAction(
                                            title = "Copy \"$adbKillStartCommandForClipboard\"",
                                            block = {
                                                copyToClipboard(adbKillStartCommandForClipboard)
                                            },
                                        ),
                                    ),
                            )
                        )
                    }

                // Pull the dump file from the device
                val dumpFileName = "dump_${hash()}.xml"
                try {
                    withTimeout(shortTimeout) {
                        deviceFileSystem.receiveFile(
                            remoteFilePath = remoteDumpFilePath,
                            destinationPath = tempDirectoryPath.resolve(dumpFileName),
                        )
                    }
                } catch (_: Exception) {
                    return@withTimeoutOrNull DumpResult.Error(
                        Notification(
                            title = "Dump File Error",
                            description =
                                "Could not pull the dump file from the device. Try again.",
                            severity = NotificationSeverity.ERROR,
                            exitStrategy = timeout(5.seconds),
                        )
                    )
                }

                // Todo: do not stop the dump if removal was unsuccessful.

                // Remove the dump file from the device
                deviceShell.executeWithTimeout(
                    command = "rm $remoteDumpFilePath",
                    commandTimeout = shortTimeout,
                    timeoutAction = {
                        return@withTimeoutOrNull DumpResult.Error(
                            Notification(
                                title = "Dump File Removal Timeout",
                                description =
                                    "Removing the dump file from the device took too long. Try again.",
                                severity = NotificationSeverity.ERROR,
                                exitStrategy = timeout(5.seconds),
                            )
                        )
                    },
                )

                dumpProgressHandler.reportXmlDumpFinished()

                val api =
                    deviceShell
                        .executeWithTimeout(
                            command = "getprop ro.build.version.sdk",
                            commandTimeout = shortTimeout,
                            timeoutAction = {
                                return@withTimeoutOrNull DumpResult.Error(
                                    Notification(
                                        title = "API Level Timeout",
                                        description = "Getting the device API level took too long.",
                                        severity = NotificationSeverity.ERROR,
                                        exitStrategy = timeout(5.seconds),
                                    )
                                )
                            },
                        )
                        .ifNonZeroExit {
                            return@withTimeoutOrNull DumpResult.Error(
                                Notification(
                                    title = "API Level Error",
                                    description = "Could not retrieve device API level.",
                                    severity = NotificationSeverity.ERROR,
                                    exitStrategy = timeout(5.seconds),
                                )
                            )
                        }
                        .stdout
                        .trim()
                        .toInt()

                val flingerCommandForClipboard = "adb shell dumpsys SurfaceFlinger --displays"

                val flingerOutput =
                    deviceShell
                        .executeWithTimeout(
                            command = "dumpsys SurfaceFlinger --displays",
                            commandTimeout = shortTimeout,
                            timeoutAction = {
                                return@withTimeoutOrNull DumpResult.Error(
                                    Notification(
                                        title = "SurfaceFlinger Timeout",
                                        description =
                                            "Getting the display IDs (SurfaceFlinger) took too long. Try again or execute \"$flingerCommandForClipboard\" to debug.",
                                        severity = NotificationSeverity.ERROR,
                                        exitStrategy = timeout(15.seconds),
                                        actions =
                                            listOf(
                                                NotificationAction(
                                                    title = "Copy \"$flingerCommandForClipboard\"",
                                                    block = {
                                                        copyToClipboard(flingerCommandForClipboard)
                                                    },
                                                )
                                            ),
                                    )
                                )
                            },
                        )
                        .ifNonZeroExit {
                            return@withTimeoutOrNull DumpResult.Error(
                                Notification(
                                    title = "SurfaceFlinger Error",
                                    description =
                                        "Could not retrieve display IDs (SurfaceFlinger failed). Try again or execute \"$flingerCommandForClipboard\" to debug.",
                                    severity = NotificationSeverity.ERROR,
                                    exitStrategy = timeout(5.seconds),
                                    actions =
                                        listOf(
                                            NotificationAction(
                                                title = "Copy \"$flingerCommandForClipboard\"",
                                                block = {
                                                    copyToClipboard(flingerCommandForClipboard)
                                                },
                                            )
                                        ),
                                )
                            )
                        }
                        .stdout

                val getDisplaysCommandForClipboard = "adb shell dumpsys SurfaceFlinger --displays"

                val getDisplaysOutput =
                    deviceShell
                        .executeWithTimeout(
                            command = "cmd display get-displays",
                            commandTimeout = shortTimeout,
                            timeoutAction = {
                                return@withTimeoutOrNull DumpResult.Error(
                                    Notification(
                                        title = "Display IDs Timeout",
                                        description =
                                            "Getting the display IDs (get-displays) took too long. Try again or execute \"$getDisplaysCommandForClipboard\" to debug.",
                                        severity = NotificationSeverity.ERROR,
                                        exitStrategy = timeout(5.seconds),
                                        actions =
                                            listOf(
                                                NotificationAction(
                                                    title =
                                                        "Copy \"$getDisplaysCommandForClipboard\"",
                                                    block = {
                                                        copyToClipboard(
                                                            getDisplaysCommandForClipboard
                                                        )
                                                    },
                                                )
                                            ),
                                    )
                                )
                            },
                        )
                        .ifNonZeroExit {
                            return@withTimeoutOrNull DumpResult.Error(
                                Notification(
                                    title = "Display IDs Error",
                                    description =
                                        "Could not retrieve display IDs (get-displays failed). Try again or execute \"$getDisplaysCommandForClipboard\" to debug.",
                                    severity = NotificationSeverity.ERROR,
                                    exitStrategy = timeout(5.seconds),
                                    actions =
                                        listOf(
                                            NotificationAction(
                                                title = "Copy \"$getDisplaysCommandForClipboard\"",
                                                block = {
                                                    copyToClipboard(getDisplaysCommandForClipboard)
                                                },
                                            )
                                        ),
                                )
                            )
                        }
                        .stdout

                val dumpOutput = tempDirectoryPath.resolve(dumpFileName).readText()

                // Contains the connections between the dump IDs and the screenshot IDs.
                val resolvedDisplays =
                    resolveDisplays(
                        api = api,
                        flingerOutput = flingerOutput,
                        getDisplaysOutput = getDisplaysOutput,
                        dumpOutput = dumpOutput,
                    )
                        ?: return@withTimeoutOrNull DumpResult.Error(
                            Notification(
                                title = "Unsupported API",
                                description = "Devices with API $api are not supported.",
                                severity = NotificationSeverity.ERROR,
                            )
                        )

                val validResolvedDisplays = resolvedDisplays.filter { it.screenshotId != null }
                dumpProgressHandler.setExpectedScreenshotCount(validResolvedDisplays.size)

                val displays = mutableListOf<Display>()
                for (resolvedDisplay in validResolvedDisplays) {
                    val screenshotFileName = "scr_${hash()}.png"
                    val remoteScreenshotFilePath = remoteScreenshotFilePath(screenshotFileName)

                    val screenshotOutput =
                        deviceShell.executeWithTimeout(
                            command =
                                "screencap -d ${resolvedDisplay.screenshotId} $remoteScreenshotFilePath",
                            commandTimeout = shortTimeout,
                            timeoutAction = {
                                return@withTimeoutOrNull DumpResult.Error(
                                    Notification(
                                        title = "Screenshot Timeout",
                                        description =
                                            "Taking a screenshot (id ${resolvedDisplay.screenshotId}) took too long. Try again.",
                                        severity = NotificationSeverity.ERROR,
                                        exitStrategy = timeout(5.seconds),
                                    )
                                )
                            },
                        )
                    // Continue if the screenshot taking process was problematic (e.g. invalid ID).
                    if (screenshotOutput.exitCode != 0) continue

                    // Todo: look into what is needed for exitCode to work as expected

                    try {
                        // Pull screenshot file from device
                        withTimeout(shortTimeout) {
                            deviceFileSystem.receiveFile(
                                remoteFilePath = remoteScreenshotFilePath,
                                destinationPath = tempDirectoryPath.resolve(screenshotFileName),
                            )
                        }
                    } catch (_: Exception) {
                        continue
                    }

                    // Remove the screenshot file from the device
                    deviceShell.executeWithTimeout(
                        command = "rm $remoteScreenshotFilePath",
                        commandTimeout = shortTimeout,
                        timeoutAction = {
                            return@withTimeoutOrNull DumpResult.Error(
                                Notification(
                                    title = "Screenshot Removal Timeout",
                                    description =
                                        "Removing the screenshot file (id ${resolvedDisplay.screenshotId}) from the device took too long. Try again.",
                                    severity = NotificationSeverity.ERROR,
                                    exitStrategy = timeout(5.seconds),
                                )
                            )
                        },
                    )

                    dumpProgressHandler.reportScreenshotTaken()

                    displays.add(
                        Display(
                            id = resolvedDisplay.dumpId,
                            screenshotFileName = screenshotFileName,
                        )
                    )
                }

                val dump =
                    Dump(
                        directoryName = "",
                        nickname = nextNickname,
                        timeMilliseconds = timeMilliseconds,
                        xmlTreeFileName = dumpFileName,
                        displays = displays,
                    )

                return@withTimeoutOrNull DumpResult.Success(dump)
            }
        }
            ?: DumpResult.Error(
                Notification(
                    title = "Dump Timeout",
                    description = "Dump process took too long (more than $dumpTimeout). Try again.",
                    severity = NotificationSeverity.ERROR,
                    exitStrategy = timeout(10.seconds),
                )
            )

    @Suppress("DuplicatedCode")
    private fun resolveDisplays(
        api: Int,
        flingerOutput: String,
        getDisplaysOutput: String,
        dumpOutput: String,
    ): List<ResolvedDisplay>? {

        // Todo: maybe use classes to offer different implementations

        val dumpDisplays =
            dumpOutput.extractAll(
                pattern = """<display id="(\d+)">""".toRegex(RegexOption.DOT_MATCHES_ALL)
            ) {
                DumpDisplay(dumpId = it.component1())
            }

        // This when statement partly contains duplicated code.
        val resolvedDisplays: List<ResolvedDisplay>? =
            when (api) {
                35 -> {
                    val flingerDisplays =
                        flingerOutput.extractAll(
                            pattern =
                                """(\w*)\s?Display (\d+)\s""".toRegex(RegexOption.DOT_MATCHES_ALL)
                        ) {
                            FlingerDisplay(
                                isVirtual = it.component1().equals("Virtual", ignoreCase = true),
                                screenshotId = it.component2(),
                            )
                        }

                    val cmdDisplays =
                        getDisplaysOutput.extractAll(
                            pattern =
                                """Display id (\d+).*?type (\w+).*?uniqueId ".*?:(\d+)""""
                                    .toRegex(RegexOption.DOT_MATCHES_ALL)
                        ) {
                            CmdDisplay(
                                dumpId = it.component1(),
                                isVirtual = it.component2().equals("VIRTUAL", ignoreCase = true),
                                screenshotId = it.component3(),
                            )
                        }

                    // Whether to resolve an equal number of virtual displays in flingerDisplays and
                    // cmdDisplays by assuming
                    // that they are listed in the same order. If this is false, virtual displays
                    // are resolved only if there
                    // is exactly one in flingerDisplays and one in cmdDisplays.
                    val resolveMultipleVirtualDisplays =
                        true // Todo: add option to select the algorithm

                    val flingerVirtuals = flingerDisplays.filter { it.isVirtual }
                    val cmdVirtuals = cmdDisplays.filter { it.isVirtual }
                    val virtualDumpToScreenshotMap: Map<String, String> =
                        if (resolveMultipleVirtualDisplays) {
                            if (flingerVirtuals.size == cmdVirtuals.size) {
                                (cmdVirtuals.map { it.dumpId }) zipMap
                                    (flingerVirtuals.map { it.screenshotId })
                            } else {
                                emptyMap()
                            }
                        } else {
                            if (flingerVirtuals.size == 1 && cmdVirtuals.size == 1) {
                                (cmdVirtuals.map { it.dumpId }) zipMap
                                    (flingerVirtuals.map { it.screenshotId })
                            } else {
                                emptyMap()
                            }
                        }

                    val nonVirtualCmdDisplays = cmdDisplays.filter { !it.isVirtual }
                    val dumpToScreenshotMap =
                        nonVirtualCmdDisplays.associate { it.dumpId to it.screenshotId } +
                            virtualDumpToScreenshotMap

                    // return
                    dumpDisplays.map {
                        ResolvedDisplay(
                            screenshotId = dumpToScreenshotMap[it.dumpId],
                            dumpId = it.dumpId,
                        )
                    }
                }

                34,
                33 -> {
                    val cmdDisplays =
                        getDisplaysOutput.extractAll(
                            pattern =
                                """Display id (\d+).*?type (\w+).*?uniqueId ".*?:(\d+)""""
                                    .toRegex(RegexOption.DOT_MATCHES_ALL)
                        ) {
                            CmdDisplay(
                                dumpId = it.component1(),
                                isVirtual = it.component2().equals("VIRTUAL", ignoreCase = true),
                                screenshotId = it.component3(),
                            )
                        }

                    val nonVirtualCmdDisplays = cmdDisplays.filter { !it.isVirtual }
                    val dumpToScreenshotMap =
                        nonVirtualCmdDisplays.associate { it.dumpId to it.screenshotId }

                    // return
                    dumpDisplays.map {
                        ResolvedDisplay(
                            screenshotId = dumpToScreenshotMap[it.dumpId],
                            dumpId = it.dumpId,
                        )
                    }
                }

                32,
                31 -> {
                    // return
                    dumpDisplays.map {
                        ResolvedDisplay(screenshotId = it.dumpId, dumpId = it.dumpId)
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
    return connectedDevices.value.firstOrNull()
        ?: run {
            connectedDevices
                .transform { devices -> emit(devices.firstOrNull()) }
                .filterNotNull()
                .first()
        }
}

fun <T> String.extractAll(pattern: Regex, transform: (MatchResult.Destructured) -> T): List<T> {
    val results = pattern.findAll(this)
    return results.toList().map { it.destructured }.map(transform)
}

infix fun <T, R> Iterable<T>.zipMap(other: Iterable<R>): Map<T, R> = zip(other).toMap()

inline fun ShellCommandOutput.ifNonZeroExit(action: () -> Unit) = apply {
    if (exitCode != 0) action()
}

suspend inline fun ShellManager.executeWithTimeout(
    command: String,
    commandTimeout: Duration,
    timeoutAction: () -> Nothing,
): ShellCommandOutput =
    withTimeoutOrNull(commandTimeout) { executeAsText(command) } ?: timeoutAction()
