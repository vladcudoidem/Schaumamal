package model.extractionManagers

import model.on
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

interface ExtractionManager {
    fun extract(): DataPaths
}

fun getExtractionManager() = on(
    win = WindowsExtractionManager,
    mac = MacosExtractionManager,
    lin = LinuxExtractionManager
)

infix fun Int.onError(action: (Int) -> Unit) {
    if (this != 0) {
        action(this)
    }
}

// TODO remove later (used for debugging)
@Suppress("unused")
@Throws(IOException::class)
fun getOutput(inputStream: InputStream): String {
    val reader = BufferedReader(InputStreamReader(inputStream))
    val output = StringBuilder()
    var line: String?

    while (reader.readLine().also { line = it } != null) {
        output.append(line).append("\n")
    }

    return output.toString()
}