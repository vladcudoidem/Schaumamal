package model.displayDataResolver

import java.io.File
import model.parser.dataClasses.DisplayNode

data class DisplayData(val screenshotFile: File, val displayNode: DisplayNode) {
    companion object {
        val Empty = DisplayData(File(""), DisplayNode.Empty)
    }
}
