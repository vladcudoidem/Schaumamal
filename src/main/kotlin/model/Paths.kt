package model

object Paths {

    const val ADB_PATH = "adb"

    const val DUMP_FILE = "ui_dump.xml"
    const val SCREENSHOT_FILE = "screenshot.png"

    const val DEVICE_FOLDER = "sdcard"
    const val LOCAL_FOLDER = "content"

    // Old TODO delete later

    const val DEVICE_DUMP_PATH = "/sdcard/$DUMP_FILE"
    const val LOCAL_DUMP_PATH = "./dump/$DUMP_FILE"

    const val DEVICE_SCREENSHOT_PATH = "/sdcard/$SCREENSHOT_FILE"
    const val LOCAL_SCREENSHOT_PATH = "./screenshot/$SCREENSHOT_FILE"
}

// TODO move to right location
infix fun String.at(subElement: String) = "$this/$subElement"