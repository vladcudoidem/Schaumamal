package oldModel

object Names {

    const val DUMP_FILE = "ui_dump.xml"
    const val SCREENSHOT_FILE = "screenshot.png"

    const val DEVICE_FOLDER = "sdcard"

    const val LOCAL_APPLICATION_FOLDER = "Schaumamal"
    const val HIDDEN_LOCAL_APPLICATION_FOLDER = ".schaumamal"
    const val LOCAL_CONTENT_FOLDER = "content"
}

// This constant allows an elegant way of starting at root while using the "at" syntax.
const val ROOT = ""

infix fun String.at(subElement: String) = "$this/$subElement"