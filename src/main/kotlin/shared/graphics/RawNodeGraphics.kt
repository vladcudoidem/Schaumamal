package shared.graphics

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import shared.xmlElements.Node

data class RawNodeGraphics(
    override val offset: Offset,
    override val size: Size
): Graphics {

    companion object {
        fun from(node: Node): RawNodeGraphics {
            val boundsValues = node.bounds
                .removeSurrounding("[", "]")
                .split("][", ",")
                .map { it.toFloat() }

            return RawNodeGraphics(
                offset = Offset(
                    x = boundsValues[0],
                    y = boundsValues[1]
                ),
                size = Size(
                    width = (boundsValues[2] - boundsValues[0]),
                    height = (boundsValues[3] - boundsValues[1])
                )
            )
        }
    }
}
