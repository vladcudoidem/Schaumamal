package model

import model.Constants.ADB_PATH
import model.Constants.DEVICE_DUMP_PATH
import model.Constants.DEVICE_SCREENSHOT_PATH
import model.Constants.LOCAL_DUMP_PATH
import model.Constants.LOCAL_SCREENSHOT_PATH
import model.Executor.executeAndWait

object LayoutExtractor {
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