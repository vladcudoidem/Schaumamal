package oldModel

import oldModel.parser.xmlElements.System
import java.io.File

data class LayoutData(
    val screenshotFile: File,
    val root: System
) {

    companion object {
        val Empty = LayoutData(screenshotFile = File(""), root = System.Empty)
    }
}
