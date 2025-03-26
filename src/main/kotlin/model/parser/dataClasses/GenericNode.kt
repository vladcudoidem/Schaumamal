package model.parser.dataClasses

data class GenericNode(
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
    override val children: List<GenericNode>,
) : Node {

    companion object {
        val Empty =
            GenericNode(
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
                children = emptyList(),
            )
    }
}
