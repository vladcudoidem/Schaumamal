package model.repository

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val maxDumps: Int
)