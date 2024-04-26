package model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

object Utils {
    private const val ADB_PATH = "adb"

    private const val DEVICE_DUMP_PATH = "/sdcard/ui_dump.xml"
    const val LOCAL_DUMP_PATH = "./dump/ui_dump.xml"

    private const val DEVICE_SCREENSHOT_PATH = "/sdcard/screenshot.png"
    const val LOCAL_SCREENSHOT_PATH = "./screenshot/screenshot.png"

    val customCoroutineScope = CoroutineScope(Dispatchers.IO + Job())
    val runtime: Runtime = Runtime.getRuntime()

    fun executeAndWait(command: String) = runtime.exec(command).waitFor()

    fun dumpXml() {
        val dumpCommand = "$ADB_PATH shell uiautomator dump --windows $DEVICE_DUMP_PATH"
        executeAndWait(dumpCommand)

        val pullCommand = "$ADB_PATH pull $DEVICE_DUMP_PATH $LOCAL_DUMP_PATH"
        executeAndWait(pullCommand)

        val removeCommand = "$ADB_PATH shell rm $DEVICE_DUMP_PATH"
        executeAndWait(removeCommand)
    }

    fun takeScreenshot() {
        val screenshotCommand = "$ADB_PATH shell screencap $DEVICE_SCREENSHOT_PATH"
        executeAndWait(screenshotCommand)

        val pullCommand = "$ADB_PATH pull $DEVICE_SCREENSHOT_PATH $LOCAL_SCREENSHOT_PATH"
        executeAndWait(pullCommand)

        val removeCommand = "$ADB_PATH shell rm $DEVICE_SCREENSHOT_PATH"
        executeAndWait(removeCommand)
    }

    fun deleteFilesOnTeardown() {
        val removeDumpCommand = "rm $LOCAL_DUMP_PATH"
        executeAndWait(removeDumpCommand)

        val removeScreenshotCommand = "rm $LOCAL_SCREENSHOT_PATH"
        executeAndWait(removeScreenshotCommand)
    }
}