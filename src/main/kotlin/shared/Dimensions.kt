package shared

import androidx.compose.ui.unit.dp

@Suppress("unused")
object Dimensions {

    /* Generic */

    val smallPadding = 5.dp
    val mediumPadding = 10.dp
    val largePadding = 20.dp

    val smallCornerRadius = 5.dp
    val mediumCornerRadius = 10.dp
    val largeCornerRadius = 20.dp

    val scrollbarThickness = 8.dp

    /* Button Layer */

    val extractButtonDiameter = 40.dp

    /* Pane Layer */

    val minimumPaneDimension = 100.dp

    // Wedge
    val wedgeSmallDimension = 6.dp
    val wedgeLargeDimension = 30.dp

    // Xml Tree Pane
    val startPaddingPerDepthLevel = 25.dp

    // Selected Node Pane
    val propertyNameWidth = 200.dp
    val maximumPropertyValueWidth = 600.dp

    /* Screenshot Layer */

    val defaultHighlighterStrokeWidth = 3.dp

    object Initial {

        /* Pane Layer */

        val initialPaneWidth = 250.dp
        val initialUpperPaneHeight = 300.dp
    }
}