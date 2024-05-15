package view

import androidx.compose.ui.unit.dp

object Dimensions {

    /* Generic */

    val smallPadding = 5.dp
    val mediumPadding = 10.dp
    val largePadding = 20.dp

    val smallCornerRadius = 5.dp
    val mediumCornerRadius = 10.dp
    val largeCornerRadius = 20.dp

    /* Specific */

    // Button Layer
    val extractButtonDiameter = 40.dp

    // Wedge
    val wedgeSmallDimension = 6.dp
    val wedgeLargeDimension = 30.dp

    // Panes
    val minimumPaneDimension = 100.dp

    // Xml Tree Pane
    val startPaddingPerDepthLevel = 25.dp

    // Selected Node Pane
    val propertyNameWidth = 200.dp
    val maximumPropertyValueWidth = 600.dp

    object Initial {
        // Panes
        val initialPaneWidth = 250.dp
        val initialUpperPaneHeight = 300.dp
    }
}