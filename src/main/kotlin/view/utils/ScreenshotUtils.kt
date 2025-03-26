package view.utils

import androidx.compose.ui.geometry.Offset
import model.parser.dataClasses.DisplayNode
import model.parser.dataClasses.GenericNode

fun DisplayNode.getNodesUnder(
    offset: Offset,
    displayPixelConversionFactor: Float,
): List<GenericNode> {
    val nodes = mutableListOf<GenericNode>()

    children.forEach { window ->
        window.children.forEach { rootNode ->
            rootNode.forThisAndDescendants { node ->
                val (nodeOffset, nodeSize) = node.getGraphics(displayPixelConversionFactor)

                val isWithinWidth = offset.x in nodeOffset.x..(nodeOffset.x + nodeSize.width)
                val isWithinHeight = offset.y in nodeOffset.y..(nodeOffset.y + nodeSize.height)

                if (isWithinWidth && isWithinHeight) {
                    nodes.add(node)
                }
            }
        }
    }

    return nodes
}

private fun GenericNode.forThisAndDescendants(action: (GenericNode) -> Unit) {
    action(this)
    children.forEach { it.forThisAndDescendants(action) }
}
