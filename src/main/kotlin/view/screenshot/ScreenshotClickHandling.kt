package view.screenshot

import androidx.compose.ui.geometry.Offset
import model.parser.Node
import model.parser.SystemNode

fun Offset.getLowestSimpleNode(system: SystemNode): Node {
    for (node in system.extractOnlySimpleNodesFlattened().reversed()) {
        if (this on node) return node
    }

    return Node.default
}

infix fun Offset.on(node: Node): Boolean {
    val nodeGraphics = NodeGraphics.from(node.bounds)

    return (x in nodeGraphics.offset.x..nodeGraphics.offset.x + nodeGraphics.size.width) &&
        (y in nodeGraphics.offset.y..nodeGraphics.offset.y + nodeGraphics.size.height)
}