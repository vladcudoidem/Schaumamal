package viewmodel

import androidx.compose.ui.geometry.Offset
import shared.xmlElements.Node
import shared.graphics.RawNodeGraphics

fun List<Node>.forNodeUnder(offset: Offset, action: (Node) -> Unit) {
    reversed().firstOrNull { offset on it }?.let { matchingNode ->
        action(matchingNode)
    }
}

infix fun Offset.on(node: Node) = with (RawNodeGraphics.from(node)) {
    val isWithinWidth = x in offset.x..(offset.x + size.width)
    val isWithinHeight = y in offset.y..(offset.y + size.height)

    // return
    isWithinWidth && isWithinHeight
}