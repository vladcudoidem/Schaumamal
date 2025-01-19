package model.platform

abstract class PlatformInformationProvider {
    abstract fun getAppDirectoryPath(): String

    abstract fun getAdbPath(): String // Todo: might no be needed anymore

    companion object {
        const val REGULAR_LOCAL_APPLICATION_FOLDER = "Schaumamal"
        const val HIDDEN_LOCAL_APPLICATION_FOLDER = ".schaumamal"
    }
}