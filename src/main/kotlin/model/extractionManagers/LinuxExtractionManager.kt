package model.extractionManagers

import model.Names.DEVICE_FOLDER
import model.Names.DUMP_FILE
import model.Names.HIDDEN_LOCAL_APPLICATION_FOLDER
import model.Names.LOCAL_CONTENT_FOLDER
import model.Names.SCREENSHOT_FILE
import model.ROOT
import model.at

object LinuxExtractionManager : ExtractionManager {

    override fun extract(): DataPaths {
        createFolder()

        return DataPaths(
            localDumpPath = dumpXml(),
            localScreenshotPath = takeScreenshot()
        )
    }

    private fun createFolder() {
        val userHomePath = System.getProperty("user.home")

        ProcessBuilder(
            "mkdir",
            userHomePath at HIDDEN_LOCAL_APPLICATION_FOLDER
        ).start().waitFor()

        ProcessBuilder(
            "mkdir",
            userHomePath at HIDDEN_LOCAL_APPLICATION_FOLDER at LOCAL_CONTENT_FOLDER
        ).start().waitFor()
    }

    private fun dumpXml(): String {
        val userHomePath = System.getProperty("user.home")
        val adbPath = userHomePath at "Android" at "Sdk" at "platform-tools" at "adb"

        ProcessBuilder(
            adbPath,
            "shell",
            "uiautomator",
            "dump",
            "--windows",
            ROOT at DEVICE_FOLDER at DUMP_FILE,
        ).start().waitFor() onError { error("Dump failed!") }

        ProcessBuilder(
            adbPath,
            "pull",
            ROOT at DEVICE_FOLDER at DUMP_FILE,
            userHomePath at HIDDEN_LOCAL_APPLICATION_FOLDER at LOCAL_CONTENT_FOLDER at DUMP_FILE
        ).start().waitFor() onError { error("Pulling dump from device failed!") }

        ProcessBuilder(
            adbPath,
            "shell",
            "rm",
            ROOT at DEVICE_FOLDER at DUMP_FILE
        ).start().waitFor() onError { error("Removing dump from device failed!") }

        return userHomePath at HIDDEN_LOCAL_APPLICATION_FOLDER at LOCAL_CONTENT_FOLDER at DUMP_FILE
    }

    private fun takeScreenshot(): String {
        val userHomePath = System.getProperty("user.home")
        val adbPath = userHomePath at "Android" at "Sdk" at "platform-tools" at "adb"

        ProcessBuilder(
            adbPath,
            "shell",
            "screencap",
            ROOT at DEVICE_FOLDER at SCREENSHOT_FILE
        ).start().waitFor() onError { error("Taking screenshot failed!") }

        ProcessBuilder(
            adbPath,
            "pull",
            ROOT at DEVICE_FOLDER at SCREENSHOT_FILE,
            userHomePath at HIDDEN_LOCAL_APPLICATION_FOLDER at LOCAL_CONTENT_FOLDER at SCREENSHOT_FILE
        ).start().waitFor() onError { error("Pulling screenshot from device failed!") }

        ProcessBuilder(
            adbPath,
            "shell",
            "rm",
            ROOT at DEVICE_FOLDER at SCREENSHOT_FILE
        ).start().waitFor() onError { error("Removing screenshot from device failed!") }

        return userHomePath at HIDDEN_LOCAL_APPLICATION_FOLDER at LOCAL_CONTENT_FOLDER at SCREENSHOT_FILE
    }
}