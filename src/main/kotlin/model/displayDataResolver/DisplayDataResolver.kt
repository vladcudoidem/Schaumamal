package model.displayDataResolver

import model.parser.XmlParser
import model.platform.PlatformInformationProvider
import model.repository.dataClasses.Dump
import kotlin.io.path.Path

class DisplayDataResolver(
    platformInformationProvider: PlatformInformationProvider,
    private val xmlParser: XmlParser
) {
    private val appDirectoryPath = Path(platformInformationProvider.getAppDirectoryPath())

    fun resolve(dumpsDirectoryName: String, selectedDump: Dump): List<DisplayData> {
        val currentDumpDirectoryPath =
            appDirectoryPath
                .resolve(dumpsDirectoryName)
                .resolve(selectedDump.directoryName)

        val dumpFile =
            currentDumpDirectoryPath
                .resolve(selectedDump.xmlTreeFileName)
                .toFile()
        val displayNodes = xmlParser.parseSystem(dumpFile)

        return selectedDump.displays.map { display ->
            DisplayData(
                screenshotFile = currentDumpDirectoryPath.resolve(display.screenshotFileName).toFile(),
                displayNode = displayNodes.first { it.id == display.id }
            )
        }
    }
}