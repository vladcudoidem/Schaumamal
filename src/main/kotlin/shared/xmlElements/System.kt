package shared.xmlElements

data class System(
    override val children: List<Display>
): XmlElement {
    // Only returns simple nodes
    fun getNodesFlattened(): List<Node> {
        val layeredMap = mutableMapOf<Int, MutableList<Node>>()
        // the index of the list represents how many levels of parent nodes its (i.e. the list's) nodes have

        children.forEach { display ->
            display.children.forEach { window ->
                window.children.forEach { rootNode ->
                    layeredMap.insertNodesLayered(node = rootNode)
                }
            }
        }

        return layeredMap.keys.sorted().flatMap { depth ->
            layeredMap[depth] ?: emptyList()
        }
    }

    // This method inserts all children (children of children as well) into the layered map (keys are depth levels).
    private fun MutableMap<Int, MutableList<Node>>.insertNodesLayered(
        node: Node,
        depth: Int = 0
    ) {
        getOrPut(depth) { mutableListOf() }.add(node)
        node.children.forEach { insertNodesLayered(node = it, depth = depth + 1) }
    }

    companion object {
        val Empty = System(children = emptyList())
    }
}
