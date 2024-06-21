package model.extractionManagers

import model.Paths.ADB_PATH
import model.Paths.DEVICE_DUMP_PATH
import model.Paths.DEVICE_SCREENSHOT_PATH
import model.Paths.LOCAL_DUMP_PATH
import model.Paths.LOCAL_SCREENSHOT_PATH
import model.utils.CommandManager.executeAndWait
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

@Deprecated("Use platform-specific execution manager.")
object OldExtractionManager {

    fun extract() {
        dumpXml()
        takeScreenshot()
    }

    private fun dumpXml() {
        val dumpCommand = "$ADB_PATH shell uiautomator dump --windows $DEVICE_DUMP_PATH"
        executeAndWait(dumpCommand)

        val pullCommand = "$ADB_PATH pull $DEVICE_DUMP_PATH $LOCAL_DUMP_PATH"
        executeAndWait(pullCommand)

        val removeCommand = "$ADB_PATH shell rm $DEVICE_DUMP_PATH"
        executeAndWait(removeCommand)
    }

    private fun takeScreenshot() {
        val screenshotCommand = "$ADB_PATH shell screencap $DEVICE_SCREENSHOT_PATH"
        executeAndWait(screenshotCommand)

        val pullCommand = "$ADB_PATH pull $DEVICE_SCREENSHOT_PATH $LOCAL_SCREENSHOT_PATH"
        executeAndWait(pullCommand)

        val removeCommand = "$ADB_PATH shell rm $DEVICE_SCREENSHOT_PATH"
        executeAndWait(removeCommand)
    }
}

interface ExtractionManager {
    fun extract(): DataPaths
}

// TODO move to right location
data class DataPaths(
    val localXmlDumpPath: String,
    val localScreenshotPath: String
)

// TODO move to right location
infix fun Int.onError(action: (Int) -> Unit) {
    if (this != 0) {
        action(this)
    }
}

// TODO remove later
@Throws(IOException::class)
fun getOutput(inputStream: InputStream): String {
    val reader = BufferedReader(InputStreamReader(inputStream))
    val output = StringBuilder()
    var line: String?

    while (reader.readLine().also { line = it } != null) {
        output.append(line).append("\n")
    }

    return output.toString()
}

// TODO refactor and move this later
fun getExtractionManager() =
    with(System.getProperty("os.name").lowercase()) {
        when {
            contains("win") -> WindowsExtractionManager
            contains("mac") -> MacosExtractionManager
            contains("nix") or contains("nux") or contains("aix") -> LinuxExtractionManager
            else -> error("Could not detect that operating system is either Windows, MacOS or Linux.")
        }
    }