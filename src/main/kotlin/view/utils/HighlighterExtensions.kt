package view.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import model.parser.dataClasses.GenericNode
import view.screenshot.Graphics

// "displayPixelConversionFactor" is for transforming the offset and size to screen pixels by
// multiplying the screenshot
// pixels with a conversion factor.
fun GenericNode.getGraphics(displayPixelConversionFactor: Float = 1.0f): Graphics {
    val bounds = bounds.removeSurrounding("[", "]").split("][", ",").map { it.toFloat() }

    val displayBounds = bounds.map { it * displayPixelConversionFactor }

    return Graphics(
        offset = Offset(x = displayBounds[0], y = displayBounds[1]),
        size =
            Size(
                width = (displayBounds[2] - displayBounds[0]),
                height = (displayBounds[3] - displayBounds[1]),
            ),
    )
}
