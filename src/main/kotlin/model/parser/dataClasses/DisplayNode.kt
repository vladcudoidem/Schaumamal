package model.parser.dataClasses

data class DisplayNode(val id: String, override val children: List<WindowNode>) : Node {
    companion object {
        val Empty = DisplayNode("-1", emptyList())
    }
}
