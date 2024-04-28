package model

import model.Constants.LOCAL_DUMP_PATH
import model.Constants.LOCAL_SCREENSHOT_PATH
import model.Executor.executeAndWait

object TeardownHelper {
    fun deleteLayoutFiles() {
        val removeDumpCommand = "rm $LOCAL_DUMP_PATH"
        executeAndWait(removeDumpCommand)

        val removeScreenshotCommand = "rm $LOCAL_SCREENSHOT_PATH"
        executeAndWait(removeScreenshotCommand)
    }
}