package model.repository

import kotlinx.serialization.Serializable

@Serializable
data class Content(
    val tempDirectoryName: String,
    val dumpsDirectoryName: String,
    val dumps: List<Dump>
)
