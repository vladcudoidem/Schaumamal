package model.platform

class MacosInformationProvider : PlatformInformationProvider() {
    private val userHomePath = System.getProperty("user.home")

    override fun getAppDirectoryPath(): String =
        userHomePath to "Library" to "Application Support" to REGULAR_LOCAL_APPLICATION_FOLDER
}
