package model.repository

import kotlinx.serialization.Serializable

@Serializable
data class Display(
    val id: String, // Is a string because some ids are very large.
    val screenshotFileName: String
)
