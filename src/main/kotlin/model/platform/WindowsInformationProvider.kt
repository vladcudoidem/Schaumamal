package model.platform

import model.to

class WindowsInformationProvider : PlatformInformationProvider() {
    private val appDataLocalPath = System.getenv("LocalAppData")

    override fun getAppDirectoryPath(): String = appDataLocalPath to REGULAR_LOCAL_APPLICATION_FOLDER

    override fun getAdbPath(): String = appDataLocalPath to "Android" to "Sdk" to "platform-tools" to "adb.exe"
}