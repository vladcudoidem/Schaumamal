package model

data class SystemNode(
    val displayNodes: List<DisplayNode>
)

data class DisplayNode(
    val id: Int,
    val windowNodes: List<WindowNodes>
)

data class WindowNodes(
    val index: Int,
    val type: String,
    val layer: Int,
    val token: String,
    val focused: Boolean,
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
)
