package oldModel.parser.xmlElements

data class Window(
    val index: Int,
    val id: Int,
    val title: String,
    val bounds: String,
    val active: Boolean,
    val type: String,
    val layer: Int,
    val focused: Boolean,
    val accessibilityFocused: Boolean,
    override val children: List<Node>
) : XmlElement
