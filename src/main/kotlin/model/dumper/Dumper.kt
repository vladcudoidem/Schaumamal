package model.dumper

import com.android.adblib.AdbSession
import com.android.adblib.ConnectedDevice
import com.android.adblib.ConnectedDevicesTracker
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
import model.repository.Content
import model.repository.Dump
import model.platform.PlatformInformationProvider
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
    private val adbSession: AdbSession
) {
    private val shortTimeout = 4.seconds
    private val dumpTimeout = 20.seconds

    private val remoteDumpFilePath = "/sdcard/dump.xml"

    private val appDirectoryPath = Path(platformInformationProvider.getAppDirectoryPath())

    init {
        appDirectoryPath.createDirectories()
    }

    @OptIn(ExperimentalPathApi::class)
    fun dump(content: Content): Dump = runBlocking(timeout = dumpTimeout) {

        // Todo: check that system health is retained if dump process is interrupted at any point

        // Dynamically retrieve any device
        val device = withTimeoutOrNull(shortTimeout) {
            adbSession.connectedDevicesTracker.waitForAnyDevice()
        } ?: error("Could not find a connected device.")

        // Attempt to root device
        withTimeoutOrNull(shortTimeout) {
            device.rootAndWait()
        } ?: error("Root process took too long..")

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
        )

        // Pull the dump file from the device
        val dumpFileName = "dump_${hash()}"
        deviceFileSystem.receiveFile(
            remoteFilePath = remoteDumpFilePath,
            destinationPath = tempDirectoryPath.resolve(dumpFileName)
        )

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
                .apply { require(exitCode == 0) { "Could not retrieve device API level." } }
                .stdout
                .toInt()

        val flingerOutput =
            deviceShell.executeAsText(
                commandTimeout = shortTimeout.toJavaDuration(),
                command = "dumpsys SurfaceFlinger --displays"
            ).stdout

        val getDisplaysOutput =
            deviceShell.executeAsText(
                commandTimeout = shortTimeout.toJavaDuration(),
                command = "cmd display get-displays"
            ).stdout

        val dumpOutput = tempDirectoryPath.resolve(dumpFileName).readText()
        val dumpDisplays = // Todo: move this to resolveDisplays()
            dumpOutput.extractAll(
                pattern = """<display id="(\d+)">""".toRegex(RegexOption.DOT_MATCHES_ALL)
            ) {
                DumpDisplay(
                    dumpId = it.component1()
                )
            }

        TODO("Continue with the resolved displays.")
    } ?: error("Dump took too long. Aborting.")

    private fun resolveDisplays(
        api: Int,
        flingerOutput: String,
        getDisplaysOutput: String,
        dumpOutput: String
    ): List<ResolvedDisplay> {

        val dumpDisplays =
            dumpOutput.extractAll(
                pattern = """<display id="(\d+)">""".toRegex(RegexOption.DOT_MATCHES_ALL)
            ) {
                DumpDisplay(
                    dumpId = it.component1()
                )
            }

        val resolvedDisplays: List<ResolvedDisplay> = when (api) {
            35 -> {
                TODO()
            }

            34 -> {
                TODO()
            }

            33 -> {
                TODO()
            }

            32 -> {
                TODO()
            }

            31 -> {
                TODO()
            }

            else -> error("Devices with API $api is not supported.")
        }

        resolvedDisplays
    }
}

// Todo: move these to separate file

private suspend fun ConnectedDevicesTracker.waitForAnyDevice(): ConnectedDevice {
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