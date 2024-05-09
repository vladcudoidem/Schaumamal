package model.parser

// TODO refactor this data class
data class SystemNode(
    val displays: List<DisplayNode>
) {
    fun extractOnlySimpleNodesFlattened(): List<Node> {
        val result = mutableListOf<Node>()
        val layeredMap = mutableMapOf<Int, MutableList<Node>>()
            // the index of the list represents how many levels of parent nodes its (the list's) nodes have

        displays.forEach { display ->
            display.windows.forEach { window ->
                window.nodes.forEach { rootNode ->
                    layeredMap.insertChildren(rootNode, 0)
                }
            }
        }

        for (depth in layeredMap.keys.sorted()) {
            layeredMap[depth]?.let { result.addAll(it) }
        }

        for (node in result.reversed().take(3)) {
            println(node.bounds)
        }

        return result
    }

    // This method inserts all children (deeply, i.e. children of children as well) into the layered map (keys are depth
    // levels).
    private fun MutableMap<Int, MutableList<Node>>.insertChildren(node: Node, depth: Int) {
        if (depth !in keys) this[depth] = mutableListOf()

        this[depth]?.add(node) // TODO why is `this[depth]` nullable?
        node.children.forEach { childNode -> insertChildren(childNode, depth + 1) }
    }

    companion object {
        val default = SystemNode(displays = emptyList())
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
    val token: String, // TODO does this exist?
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
        val default = Node(
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
