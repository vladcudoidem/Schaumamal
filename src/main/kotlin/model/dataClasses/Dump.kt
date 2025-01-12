package model.dataClasses

import kotlinx.serialization.Serializable

@Serializable
data class Dump(
    val directoryName: String,
    val nickname: String,
    val milliseconds: Long,
    val xmlTreeFileName: String,
    val displays: List<Display>
)
