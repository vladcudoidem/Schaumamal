package model.displayDataResolver

import model.parser.dataClasses.DisplayNode
import java.io.File

data class DisplayData(
    val screenshotFile: File,
    val displayNode: DisplayNode
) {
    companion object {
        val Empty = DisplayData(File(""), DisplayNode.Empty)
    }
}
