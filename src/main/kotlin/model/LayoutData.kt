package model

import model.parser.SystemNode

data class LayoutData(
    val screenshotPath: String,
    val root: SystemNode
) {

    companion object {
        val empty = LayoutData(screenshotPath = "", root = SystemNode.empty)
    }
}
