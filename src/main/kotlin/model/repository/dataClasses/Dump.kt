package model.repository.dataClasses

import kotlinx.serialization.Serializable

@Serializable
data class Dump(
    val directoryName: String,
    val nickname: String,
    val timeMilliseconds: Long,
    val xmlTreeFileName: String,
    val displays: List<Display>
)
