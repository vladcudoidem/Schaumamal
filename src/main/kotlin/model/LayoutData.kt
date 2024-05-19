package model

import model.parser.xmlElements.System

data class LayoutData(
    val screenshotPath: String,
    val root: System
) {

    companion object {
        val Empty = LayoutData(screenshotPath = "", root = System.Empty)
    }
}
