package model.parser.dataClasses

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class DisplayNode(val id: String, override val children: List<WindowNode>) : Node {
    @OptIn(ExperimentalUuidApi::class)
    override val uuid: Uuid = Uuid.random()

    companion object {
        val Empty = DisplayNode("-1", emptyList())
    }
}
