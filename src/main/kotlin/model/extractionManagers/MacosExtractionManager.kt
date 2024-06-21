package model.extractionManagers

import model.Paths.DEVICE_FOLDER
import model.Paths.DUMP_FILE
import model.Paths.LOCAL_APPLICATION_FOLDER
import model.Paths.LOCAL_CONTENT_FOLDER
import model.Paths.SCREENSHOT_FILE
import model.at

// TODO refactor this object
object MacosExtractionManager : ExtractionManager {
    override fun extract(): DataPaths {
        createFolder()

        return DataPaths(
            localXmlDumpPath = dumpXml(),
            localScreenshotPath = takeScreenshot()
        )
    }

    private fun createFolder() {
        val userHome = System.getProperty("user.home")
        val applicationSupport = userHome at "Library" at "Application Support"

        val process1 = ProcessBuilder(
            "mkdir",
            applicationSupport at LOCAL_APPLICATION_FOLDER
        ).start()
        process1.waitFor() onError { }

        val process2 = ProcessBuilder(
            "mkdir",
            applicationSupport at LOCAL_APPLICATION_FOLDER at LOCAL_CONTENT_FOLDER
        ).start()
        process2.waitFor() onError { }
    }

    private fun dumpXml(): String {
        val userHome = System.getProperty("user.home")
        val applicationSupport = userHome at "Library" at "Application Support"

        val adbPath = userHome at "Library" at "Android" at "sdk" at "platform-tools" at "adb"

        ProcessBuilder(
            adbPath,
            "shell",
            "uiautomator",
            "dump",
            "--windows",
            "/$DEVICE_FOLDER" at DUMP_FILE
        ).start().waitFor() onError { error("dump") }

        ProcessBuilder(
            adbPath,
            "pull",
            "/$DEVICE_FOLDER" at DUMP_FILE,
            applicationSupport at LOCAL_APPLICATION_FOLDER at LOCAL_CONTENT_FOLDER at DUMP_FILE
        ).start().waitFor() onError { error("dump pull") }

        return applicationSupport at LOCAL_APPLICATION_FOLDER at LOCAL_CONTENT_FOLDER at DUMP_FILE
    }

    private fun takeScreenshot(): String {
        val userHome = System.getProperty("user.home")
        val applicationSupport = userHome at "Library" at "Application Support"

        val adbPath = userHome at "Library" at "Android" at "sdk" at "platform-tools" at "adb"

        ProcessBuilder(
            adbPath,
            "shell",
            "screencap",
            "/$DEVICE_FOLDER" at SCREENSHOT_FILE
        ).start().waitFor() onError { error("screencap") }

        ProcessBuilder(
            adbPath,
            "pull",
            "/$DEVICE_FOLDER" at SCREENSHOT_FILE,
            applicationSupport at LOCAL_APPLICATION_FOLDER at LOCAL_CONTENT_FOLDER at SCREENSHOT_FILE
        ).start().waitFor() onError { error("screencap pull") }

        return applicationSupport at LOCAL_APPLICATION_FOLDER at LOCAL_CONTENT_FOLDER at SCREENSHOT_FILE
    }
}