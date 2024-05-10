package model.utils

import model.utils.CommandConstants.LOCAL_DUMP_PATH
import model.utils.CommandConstants.LOCAL_SCREENSHOT_PATH
import model.utils.CommandExecutor.executeAndWait

object TeardownManager {

    fun deleteLayoutFiles() {
        val removeDumpCommand = "rm $LOCAL_DUMP_PATH"
        executeAndWait(removeDumpCommand)

        val removeScreenshotCommand = "rm $LOCAL_SCREENSHOT_PATH"
        executeAndWait(removeScreenshotCommand)
    }
}