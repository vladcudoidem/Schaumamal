package model.parser

data class SystemNode(
    val displays: List<DisplayNode>
) {
    // Only returns simple nodes
    fun getNodesFlattened(): List<Node> {
        val layeredMap = mutableMapOf<Int, MutableList<Node>>()
            // the index of the list represents how many levels of parent nodes its (i.e. the list's) nodes have

        displays.forEach { display ->
            display.windows.forEach { window ->
                window.nodes.forEach { rootNode ->
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
        val empty = SystemNode(displays = emptyList())
    }
}

data class DisplayNode(
    val id: Int,
    val windows: List<WindowNode>
)

data class WindowNode(
    val index: Int,
    val id: Int,
    val title: String,
    val bounds: String,
    val active: Boolean,
    val type: String,
    val layer: Int,
    val token: String, // TODO this property might not exist
    val focused: Boolean,
    val accessibilityFocused: Boolean,
    val nodes: List<Node>
)

data class Node(
    val index: Int,
    val text: String,
    val resourceId: String,
    val className: String,
    val packageName: String,
    val contentDesc: String,
    val checkable: Boolean,
    val checked: Boolean,
    val clickable: Boolean,
    val enabled: Boolean,
    val focusable: Boolean,
    val focused: Boolean,
    val scrollable: Boolean,
    val longClickable: Boolean,
    val password: Boolean,
    val selected: Boolean,
    val bounds: String,
    val children: List<Node>
) {

    companion object {
        val empty = Node(
            index = -1,
            text = "",
            resourceId = "",
            className = "",
            packageName = "",
            contentDesc = "",
            checkable = false,
            checked = false,
            clickable = false,
            enabled = false,
            focusable = false,
            focused = false,
            scrollable = false,
            longClickable = false,
            password = false,
            selected = false,
            bounds = "[0,0][0,0]",
            children = emptyList()
        )
    }
}
