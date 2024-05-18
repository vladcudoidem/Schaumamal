package model.utils

import model.utils.CommandManager.executeAndWait
import model.utils.Path.LOCAL_DUMP_PATH
import model.utils.Path.LOCAL_SCREENSHOT_PATH

object TeardownManager {

    fun deleteLayoutFiles() {
        val removeDumpCommand = "rm $LOCAL_DUMP_PATH"
        executeAndWait(removeDumpCommand)

        val removeScreenshotCommand = "rm $LOCAL_SCREENSHOT_PATH"
        executeAndWait(removeScreenshotCommand)
    }
}