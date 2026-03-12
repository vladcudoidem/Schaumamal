package model.parser.dataClasses

import kotlin.uuid.Uuid

data class DisplayNode(
    val id: String,
    override val children: List<WindowNode>,
    override val uuid: Uuid = Uuid.random(),
) : Node {
    companion object {
        val Empty = DisplayNode("-1", emptyList())
    }
}
