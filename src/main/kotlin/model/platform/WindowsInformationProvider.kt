package model.platform

class WindowsInformationProvider : PlatformInformationProvider() {
    private val appDataLocalPath = System.getenv("LocalAppData")

    override fun getAppDirectoryPath(): String = appDataLocalPath to REGULAR_LOCAL_APPLICATION_FOLDER
}