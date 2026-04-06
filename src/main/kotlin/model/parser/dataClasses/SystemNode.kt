package model.parser.dataClasses

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class SystemNode(override val children: List<DisplayNode>) : Node {
    @OptIn(ExperimentalUuidApi::class)
    override val uuid: Uuid = Uuid.random()

    companion object {
        val Empty = SystemNode(children = emptyList())
    }
}
