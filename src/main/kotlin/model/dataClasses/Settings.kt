package model.dataClasses

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val maxDumps: Int
)
