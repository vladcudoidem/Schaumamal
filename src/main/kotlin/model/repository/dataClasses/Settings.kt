package model.repository.dataClasses

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val maxDumps: Int
) {
    companion object {
        val DefaultEmpty = Settings(
            maxDumps = 3
        )
    }
}
