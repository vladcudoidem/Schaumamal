package model.extractionManagers

object LinuxExtractionManager : ExtractionManager {
    override fun extract(): DataPaths {
        return DataPaths("", "")
    }
}