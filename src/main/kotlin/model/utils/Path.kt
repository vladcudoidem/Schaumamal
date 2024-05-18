package model.utils

object Path {

    const val ADB_PATH = "adb"

    private const val DUMP_FILE = "ui_dump.xml"
    const val DEVICE_DUMP_PATH = "/sdcard/$DUMP_FILE"
    const val LOCAL_DUMP_PATH = "./dump/$DUMP_FILE"

    private const val SCREENSHOT_FILE = "screenshot.png"
    const val DEVICE_SCREENSHOT_PATH = "/sdcard/$SCREENSHOT_FILE"
    const val LOCAL_SCREENSHOT_PATH = "./screenshot/$SCREENSHOT_FILE"
}