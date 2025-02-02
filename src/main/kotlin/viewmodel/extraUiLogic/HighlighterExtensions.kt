package viewmodel.extraUiLogic

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import model.parser.xmlElements.GenericNode
import viewmodel.Graphics

// "Display" means that the offset and size have already been transformed to screen pixels by multiplying the screenshot
// pixels with a conversion factor.
fun GenericNode.extractDisplayGraphics(displayPixelConversionFactor: Float): Graphics {
    val bounds = bounds
        .removeSurrounding("[", "]")
        .split("][", ",")
        .map { it.toFloat() }

    val displayBounds = bounds.map { bound -> bound * displayPixelConversionFactor }

    return Graphics(
        offset = Offset(
            x = displayBounds[0],
            y = displayBounds[1]
        ),
        size = Size(
            width = (displayBounds[2] - displayBounds[0]),
            height = (displayBounds[3] - displayBounds[1])
        )
    )
}