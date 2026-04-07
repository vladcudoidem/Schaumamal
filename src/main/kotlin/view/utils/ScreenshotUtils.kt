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
                val displayBounds = node.bounds * displayPixelConversionFactor

                val isWithinWidth =
                    offset.x in displayBounds.x..(displayBounds.x + displayBounds.width)
                val isWithinHeight =
                    offset.y in displayBounds.y..(displayBounds.y + displayBounds.height)

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
