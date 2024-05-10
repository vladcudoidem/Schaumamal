package view.screenshot

import androidx.compose.ui.geometry.Offset
import model.parser.Node

fun List<Node>.forNodeUnder(offset: Offset, action: (Node) -> Unit) {
    reversed().firstOrNull { offset on it }?.let { matchingNode ->
        action(matchingNode)
    }
}

infix fun Offset.on(node: Node) = with (NodeGraphics.from(node)) {
    val isWithinWidth = x in offset.x..(offset.x + size.width)
    val isWithinHeight = y in offset.y..(offset.y + size.height)

    // return
    isWithinWidth && isWithinHeight
}