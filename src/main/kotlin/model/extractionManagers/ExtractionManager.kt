package model.extractionManagers

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

interface ExtractionManager {
    fun extract(): DataPaths
}

fun getExtractionManager() =
    with(System.getProperty("os.name").lowercase()) {
        when {
            contains("win") -> WindowsExtractionManager
            contains("mac") -> MacosExtractionManager
            contains("nix") or contains("nux") or contains("aix") -> LinuxExtractionManager
            else -> error("Could not detect that operating system is either Windows, MacOS or Linux.")
        }
    }

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