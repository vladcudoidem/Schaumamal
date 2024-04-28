package model

object Constants {
    const val ADB_PATH = "adb"

    const val DUMP_FILE = "ui_dump.xml"
    const val DEVICE_DUMP_PATH = "/sdcard/$DUMP_FILE"
    const val LOCAL_DUMP_PATH = "./dump/$DUMP_FILE"

    const val SCREENSHOT_FILE = "screenshot.png"
    const val DEVICE_SCREENSHOT_PATH = "/sdcard/$SCREENSHOT_FILE"
    const val LOCAL_SCREENSHOT_PATH = "./screenshot/$SCREENSHOT_FILE"
}