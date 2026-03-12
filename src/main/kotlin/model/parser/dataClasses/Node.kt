package model.parser.dataClasses

import kotlin.uuid.Uuid

interface Node {
    val children: List<Node>
    val uuid: Uuid
}
