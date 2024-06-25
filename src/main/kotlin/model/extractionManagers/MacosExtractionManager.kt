package model.extractionManagers

import model.Names.DEVICE_FOLDER
import model.Names.DUMP_FILE
import model.Names.LOCAL_APPLICATION_FOLDER
import model.Names.LOCAL_CONTENT_FOLDER
import model.Names.SCREENSHOT_FILE
import model.ROOT
import model.at

object MacosExtractionManager : ExtractionManager {

    override fun extract(): DataPaths {
        createFolder()

        return DataPaths(
            localDumpPath = dumpXml(),
            localScreenshotPath = takeScreenshot()
        )
    }

    private fun createFolder() {
        val userHomePath = System.getProperty("user.home")
        val applicationSupportPath = userHomePath at "Library" at "Application Support"

        ProcessBuilder(
            "mkdir",
            applicationSupportPath at LOCAL_APPLICATION_FOLDER
        ).start().waitFor()

        ProcessBuilder(
            "mkdir",
            applicationSupportPath at LOCAL_APPLICATION_FOLDER at LOCAL_CONTENT_FOLDER
        ).start().waitFor()
    }

    private fun dumpXml(): String {
        val userHomePath = System.getProperty("user.home")
        val applicationSupportPath = userHomePath at "Library" at "Application Support"
        val adbPath = userHomePath at "Library" at "Android" at "sdk" at "platform-tools" at "adb"

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
            applicationSupportPath at LOCAL_APPLICATION_FOLDER at LOCAL_CONTENT_FOLDER at DUMP_FILE
        ).start().waitFor() onError { error("Pulling dump from device failed!") }

        ProcessBuilder(
            adbPath,
            "shell",
            "rm",
            ROOT at DEVICE_FOLDER at DUMP_FILE
        ).start().waitFor() onError { error("Removing dump from device failed!") }

        return applicationSupportPath at LOCAL_APPLICATION_FOLDER at LOCAL_CONTENT_FOLDER at DUMP_FILE
    }

    private fun takeScreenshot(): String {
        val userHomePath = System.getProperty("user.home")
        val applicationSupportPath = userHomePath at "Library" at "Application Support"
        val adbPath = userHomePath at "Library" at "Android" at "sdk" at "platform-tools" at "adb"

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
            applicationSupportPath at LOCAL_APPLICATION_FOLDER at LOCAL_CONTENT_FOLDER at SCREENSHOT_FILE
        ).start().waitFor() onError { error("Pulling screenshot from device failed!") }

        ProcessBuilder(
            adbPath,
            "shell",
            "rm",
            ROOT at DEVICE_FOLDER at SCREENSHOT_FILE
        ).start().waitFor() onError { error("Removing screenshot from device failed!") }

        return applicationSupportPath at LOCAL_APPLICATION_FOLDER at LOCAL_CONTENT_FOLDER at SCREENSHOT_FILE
    }
}