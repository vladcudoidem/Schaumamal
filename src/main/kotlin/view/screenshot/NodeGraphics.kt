package view.screenshot

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

data class NodeGraphics(
    override val offset: Offset,
    override val size: Size
): Graphics {

    companion object {
        // TODO refactor to receive a Node, not a String
        fun from(bounds: String): NodeGraphics { // bounds are a string in the xml dump
            val boundsValues = bounds
                .split("][", "[", "]", ",")
                .filter { it.isNotEmpty() }
                .map { it.toInt() }

            return NodeGraphics(
                offset = Offset(
                    x = boundsValues[0].toFloat(),
                    y = boundsValues[1].toFloat()
                ),
                size = Size(
                    width = (boundsValues[2] - boundsValues[0]).toFloat(),
                    height = (boundsValues[3] - boundsValues[1]).toFloat()
                )
            )
        }
    }
}
