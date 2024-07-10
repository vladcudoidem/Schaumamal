package zlayground

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import model.at
import java.io.File

/*
* Todo:
*  - JSON manipulation (also: outdated/new properties)
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