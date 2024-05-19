package model

import model.parser.xmlElements.System
import model.utils.Path.LOCAL_SCREENSHOT_PATH
import java.io.File

data class LayoutData(
    val screenshotFile: File,
    val root: System
) {

    companion object {
        val Empty = LayoutData(screenshotFile = File(LOCAL_SCREENSHOT_PATH), root = System.Empty)
    }
}
