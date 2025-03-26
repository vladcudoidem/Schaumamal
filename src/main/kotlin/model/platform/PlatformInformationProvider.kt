package model.platform

import model.on

abstract class PlatformInformationProvider {
    abstract fun getAppDirectoryPath(): String

    companion object {
        const val REGULAR_LOCAL_APPLICATION_FOLDER = "Schaumamal"
        const val HIDDEN_LOCAL_APPLICATION_FOLDER = ".schaumamal"

        fun current() =
            on(
                win = WindowsInformationProvider(),
                lin = LinuxInformationProvider(),
                mac = MacosInformationProvider(),
            )
    }
}
