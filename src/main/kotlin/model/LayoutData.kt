package model

import model.parser.SystemNode

data class LayoutData(
    val screenshotPath: String,
    val root: SystemNode
) {
    companion object {
        val default = LayoutData(screenshotPath = "", root = SystemNode.default)
    }
}
