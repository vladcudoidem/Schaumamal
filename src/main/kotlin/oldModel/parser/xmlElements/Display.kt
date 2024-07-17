package oldModel.parser.xmlElements

data class Display(
    val id: Int,
    override val children: List<Window>
) : XmlElement
