package model.utils

import model.utils.CommandManager.Constants.LOCAL_DUMP_PATH
import model.utils.CommandManager.Constants.LOCAL_SCREENSHOT_PATH
import model.utils.CommandManager.executeAndWait

object TeardownManager {

    fun deleteLayoutFiles() {
        val removeDumpCommand = "rm $LOCAL_DUMP_PATH"
        executeAndWait(removeDumpCommand)

        val removeScreenshotCommand = "rm $LOCAL_SCREENSHOT_PATH"
        executeAndWait(removeScreenshotCommand)
    }
}