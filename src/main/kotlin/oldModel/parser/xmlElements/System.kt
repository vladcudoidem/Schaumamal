package oldModel.parser.xmlElements

data class System(
    override val children: List<Display>
) : XmlElement {

    companion object {
        val Empty = System(children = emptyList())
    }
}
