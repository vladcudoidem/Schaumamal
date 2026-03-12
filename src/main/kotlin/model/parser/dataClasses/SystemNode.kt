package model.parser.dataClasses

import kotlin.uuid.Uuid

data class SystemNode(
    override val children: List<DisplayNode>,
    override val uuid: Uuid = Uuid.random(),
) : Node {
    companion object {
        val Empty = SystemNode(children = emptyList())
    }
}
