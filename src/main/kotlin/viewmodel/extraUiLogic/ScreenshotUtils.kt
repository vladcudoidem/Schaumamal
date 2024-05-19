package viewmodel.extraUiLogic

import androidx.compose.ui.geometry.Offset
import model.parser.xmlElements.Node
import model.parser.xmlElements.System

fun List<Node>.forFirstNodeUnder(
    offset: Offset,
    displayPixelConversionFactor: Float,
    action: (Node) -> Unit
) {
    val firstMatchingNode = firstOrNull { node ->
        val (nodeOffset, nodeSize) = node.extractDisplayGraphics(displayPixelConversionFactor)

        val isWithinWidth = offset.x in nodeOffset.x..(nodeOffset.x + nodeSize.width)
        val isWithinHeight = offset.y in nodeOffset.y..(nodeOffset.y + nodeSize.height)

        isWithinWidth && isWithinHeight
    }

    if (firstMatchingNode != null) action(firstMatchingNode)
}

// Only returns simple nodes.
fun System.getNodesOrderedByDepth(deepNodesFirst: Boolean = false): List<Node> {
    // The index of the list represents how many levels of parent nodes its (i.e. the list's) nodes have.
    val layeredMap = mutableMapOf<Int, MutableList<Node>>()

    children.forEach { display ->
        display.children.forEach { window ->
            window.children.forEach { rootNode ->
                layeredMap.insertAllNodesUnder(node = rootNode)
            }
        }
    }

    val nodesByGrowingDepth = layeredMap.keys.sorted().flatMap { depth ->
        layeredMap[depth]!!
    }

    return if (deepNodesFirst) {
        nodesByGrowingDepth.reversed()
    } else {
        nodesByGrowingDepth
    }
}

// This method inserts all children (children of children as well) into the layered map (keys are depth levels).
private fun MutableMap<Int, MutableList<Node>>.insertAllNodesUnder(
    node: Node,
    depth: Int = 0
) {
    getOrPut(depth) { mutableListOf() }.add(node)
    node.children.forEach { insertAllNodesUnder(node = it, depth = depth + 1) }
}