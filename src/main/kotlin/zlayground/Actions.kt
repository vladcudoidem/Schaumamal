package zlayground

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.at
import java.io.File

/*
* Todo:
*  - JSON manipulation (also: outdated/new properties) -> Done!
*  - ADB (maybe with some library?)
* */

/* Utils */

var index = 0
var output by mutableStateOf("$index")

fun o(text: String) {
    index++
    output = "$index - $text"
}

fun o(t: Boolean) {
    o(t.toString())
}

val appDir get() = System.getProperty("user.home") at "Library" at "Application Support" at "Schaumamal"
val appFolder = File(appDir)

/* Exp Folder */

val expFolder = File(appFolder, "exp")

fun checkFolderExistence() {
    o((expFolder.exists() && expFolder.isDirectory))
}

fun createFolder() {
    o(expFolder.mkdir())
}

fun deleteFolder() {
    o(expFolder.delete())
}

/* Json */

val jsonFile = File(expFolder, "config.json")

fun checkJsonExistence() {
    o(jsonFile.exists() && jsonFile.isFile)
}

fun createJson() {
    if (!jsonFile.parentFile.exists()) {
        o("Folder does not exist")
    } else {
        o(jsonFile.createNewFile())
    }
}

fun deleteJson() {
    o(jsonFile.delete())
}

/* Direct Json */

// Isn't of much use. Instead, I will always have to check the existence of the parent folders.

val jsonFileDirect = File("$appDir/exp/config.json")

fun checkDirectJsonExistence() {
    o(jsonFileDirect.exists() && jsonFileDirect.isFile)
}

fun createDirectJson() {
    o(jsonFileDirect.createNewFile())
}

fun deleteDirectJson() {
    o(jsonFileDirect.delete())
}

/* Json manipulation */

val json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
}

fun saveJsonData() {
    val config = Config(
        version = 1,
        count = 3,
        data = listOf(
            Dump("1"),
            Dump("2"),
            Dump("3"),
            Dump("4"),
            Dump("5")
        )
    )

    o(config.toString())
    jsonFile.writeText(json.encodeToString(config))
}

fun readJsonData() {
    val configText = jsonFile.readText()
    val config = json.decodeFromString<Config>(configText)

    o(config.toString())
}

fun readChangeWriteJson() {
    val configText = jsonFile.readText()
    val config = json.decodeFromString<Config>(configText).copy(version = 222)

    o(config.toString())
    jsonFile.writeText(json.encodeToString(config))
}

fun readNewJsonData() {
    val configText = jsonFile.readText()
    val config = json.decodeFromString<NewConfig>(configText)

    o(config.toString())
}