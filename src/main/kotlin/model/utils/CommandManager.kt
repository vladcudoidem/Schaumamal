package model.utils

object CommandManager {

    private val runtime: Runtime = Runtime.getRuntime()

    fun executeAndWait(command: String) = runtime.exec(command).waitFor()

    object Constants {

        const val ADB_PATH = "adb"

        private const val DUMP_FILE = "ui_dump.xml"
        const val DEVICE_DUMP_PATH = "/sdcard/$DUMP_FILE"
        const val LOCAL_DUMP_PATH = "./dump/$DUMP_FILE"

        private const val SCREENSHOT_FILE = "screenshot.png"
        const val DEVICE_SCREENSHOT_PATH = "/sdcard/$SCREENSHOT_FILE"
        const val LOCAL_SCREENSHOT_PATH = "./screenshot/$SCREENSHOT_FILE"
    }
}