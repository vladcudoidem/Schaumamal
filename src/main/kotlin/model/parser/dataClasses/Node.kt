package model.parser.dataClasses

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface Node {
    @OptIn(ExperimentalUuidApi::class) val uuid: Uuid

    val children: List<Node>
}
