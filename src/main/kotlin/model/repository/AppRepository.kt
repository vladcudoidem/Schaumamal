package model.repository

import kotlinx.serialization.json.Json
import model.platform.PlatformInformationProvider
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import kotlin.io.path.forEachDirectoryEntry
import kotlin.io.path.moveTo
import kotlin.io.path.name
import kotlin.io.path.readText
import kotlin.io.path.writeText

// Do not remove! This is needed.
import kotlinx.serialization.encodeToString

class AppRepository(
    platformInformationProvider: PlatformInformationProvider
) {
    private val appDirectoryPath = Path(platformInformationProvider.getAppDirectoryPath())

    init {
        appDirectoryPath.createDirectories()
    }

    private val contentJsonFilePath = appDirectoryPath.resolve("content.json")
    private val settingsFilePath = appDirectoryPath.resolve("settings.json")

    fun existsContentJson(): Boolean = contentJsonFilePath.exists()

    fun readContentJson() = contentJsonFilePath.readJson<Content>()

    fun writeContentJson(content: Content) = contentJsonFilePath.writeJson(content)

    fun existsSettingsJson(): Boolean = settingsFilePath.exists()

    fun readSettingsJson() = settingsFilePath.readJson<Settings>()

    fun writeSettingsJson(settings: Settings) = settingsFilePath.writeJson(settings)

    private inline fun <reified T> Path.readJson(): T {
        require(exists()) { "$name does not exist and thus cannot be read." }

        val jsonString = readText()
        val jsonObject = Json.decodeFromString<T>(jsonString)

        return jsonObject
    }

    private inline fun <reified T> Path.writeJson(jsonObject: T) {
        val jsonString = Json.encodeToString(jsonObject)
        writeText(jsonString)
    }

    fun createContentDirectories(content: Content) {
        Path(content.tempDirectoryName).createDirectories()
        Path(content.dumpsDirectoryName).createDirectories()
    }

    fun registerNewDump(dump: Dump, content: Content, settings: Settings): Content {
        val tempDirectoryPath = appDirectoryPath.resolve(content.tempDirectoryName)

        val currentDumpCount = content.dumps.size
        val maxDumpsReached = currentDumpCount >= settings.maxDumps
        val destinationDirectoryPath =
            if (maxDumpsReached) {
                appDirectoryPath
                    .resolve(content.dumps.last().directoryName)
            } else {
                appDirectoryPath
                    .resolve(content.dumpsDirectoryName)
                    .resolve(currentDumpCount.inc().toString())
            }

        // Also clears the destination and temp directories.
        tempDirectoryPath.moveContents(destinationDirectoryPath)

        val updatedDump = dump.copy(directoryName = destinationDirectoryPath.name)
        val updatedDumps =
            content.dumps
                .let { if (maxDumpsReached) it.dropLast(1) else it }
                .toMutableList()
                .apply { add(0, updatedDump) }
                .toList()
        val updatedContent = content.copy(dumps = updatedDumps)

        return updatedContent
    }

    @OptIn(ExperimentalPathApi::class)
    private fun Path.moveContents(destination: Path) {
        destination.deleteRecursively()
        destination.createDirectories()

        // This only works for destinations that contain regular files and no further directories.
        this.forEachDirectoryEntry {
            it.moveTo(destination)
        }

        this.deleteRecursively()
        this.createDirectories()
    }
}