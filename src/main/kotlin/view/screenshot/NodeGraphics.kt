package view.screenshot

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import model.parser.Node

data class NodeGraphics(
    override val offset: Offset,
    override val size: Size
): Graphics {

    companion object {
        fun from(node: Node): NodeGraphics {
            val boundsValues = node.bounds
                .removeSurrounding("[", "]")
                .split("][", ",")
                .map { it.toFloat() }

            return NodeGraphics(
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
