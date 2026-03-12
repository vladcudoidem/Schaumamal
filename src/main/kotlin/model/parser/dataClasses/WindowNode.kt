package model.parser.dataClasses

import kotlin.uuid.Uuid

data class WindowNode(
    val index: Int,
    val id: String,
    val title: String,
    val bounds: String,
    val active: Boolean,
    val type: String,
    val layer: Int,
    val focused: Boolean,
    val accessibilityFocused: Boolean,
    override val children: List<GenericNode>,
    override val uuid: Uuid = Uuid.random(),
) : Node
