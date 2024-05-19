package shared.xmlElements

data class Window(
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
    override val children: List<Node>
): XmlElement
