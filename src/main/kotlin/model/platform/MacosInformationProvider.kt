package model.platform

import model.to

class MacosInformationProvider : PlatformInformationProvider() {
    private val userHomePath = System.getProperty("user.home")

    override fun getAppDirectoryPath(): String =
        userHomePath to "Library" to "Application Support" to REGULAR_LOCAL_APPLICATION_FOLDER

    override fun getAdbPath(): String = userHomePath to "Library" to "Android" to "sdk" to "platform-tools" to "adb"
}