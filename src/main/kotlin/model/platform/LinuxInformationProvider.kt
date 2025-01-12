package model.platform

import model.to

class LinuxInformationProvider : PlatformInformationProvider() {
    private val userHomePath = System.getProperty("user.home")

    override fun getAppDirectoryPath(): String = userHomePath to HIDDEN_LOCAL_APPLICATION_FOLDER

    override fun getAdbPath(): String = userHomePath to "Android" to "Sdk" to "platform-tools" to "adb"
}