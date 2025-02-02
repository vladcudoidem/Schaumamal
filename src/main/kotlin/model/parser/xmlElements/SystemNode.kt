package model.parser.xmlElements

data class SystemNode(
    override val children: List<DisplayNode>
) : Node {

    companion object {
        val Empty = SystemNode(children = emptyList())
    }
}
