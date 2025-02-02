package model.repository.dataClasses

import kotlinx.serialization.Serializable

@Serializable
data class Content(
    val tempDirectoryName: String,
    val dumpsDirectoryName: String,
    val dumps: List<Dump>
) {
    companion object {
        val DefaultEmpty = Content(
            tempDirectoryName = "tmp",
            dumpsDirectoryName = "dumps",
            dumps = emptyList()
        )
    }
}
