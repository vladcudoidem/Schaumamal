package zlayground

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val version: Int,
    val count: Int,
    val data: List<Dump>
)

@Serializable
data class NewConfig(
    val version: Int,
    val author: String = "Zlayground",
    val count: Int,
    val data: List<Dump>
)

@Serializable
data class Dump(
    val name: String
)