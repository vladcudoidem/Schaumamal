package model.platform

class LinuxInformationProvider : PlatformInformationProvider() {
    private val userHomePath = System.getProperty("user.home")

    override fun getAppDirectoryPath(): String = userHomePath to HIDDEN_LOCAL_APPLICATION_FOLDER
}
