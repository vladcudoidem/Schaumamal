package oldModel.extractionManagers

import oldModel.Names.DEVICE_FOLDER
import oldModel.Names.DUMP_FILE
import oldModel.Names.LOCAL_APPLICATION_FOLDER
import oldModel.Names.LOCAL_CONTENT_FOLDER
import oldModel.Names.SCREENSHOT_FILE
import oldModel.ROOT
import oldModel.at

object WindowsExtractionManager : ExtractionManager {

    override fun extract(): DataPaths {
        createFolder()

        return DataPaths(
            localDumpPath = dumpXml(),
            localScreenshotPath = takeScreenshot()
        )
    }

    private fun createFolder() {
        val appDataLocalPath = System.getenv("LocalAppData")

        ProcessBuilder(
            "cmd.exe",
            "/c",
            "mkdir",
            "\"${appDataLocalPath at LOCAL_APPLICATION_FOLDER}\""
        ).start().waitFor()

        ProcessBuilder(
            "cmd.exe",
            "/c",
            "mkdir",
            "\"${appDataLocalPath at LOCAL_APPLICATION_FOLDER at LOCAL_CONTENT_FOLDER}\""
        ).start().waitFor()
    }

    private fun dumpXml(): String {
        val appDataLocalPath = System.getenv("LocalAppData")
        val adbPath = appDataLocalPath at "Android" at "Sdk" at "platform-tools" at "adb.exe"

        ProcessBuilder(
            adbPath,
            "shell",
            "uiautomator",
            "dump",
            "--windows",
            ROOT at DEVICE_FOLDER at DUMP_FILE
        ).start().waitFor() onError { error("Dump failed!") }

        ProcessBuilder(
            adbPath,
            "pull",
            ROOT at DEVICE_FOLDER at DUMP_FILE,
            appDataLocalPath at LOCAL_APPLICATION_FOLDER at LOCAL_CONTENT_FOLDER at DUMP_FILE
        ).start().waitFor() onError { error("Pulling dump from device failed!") }

        ProcessBuilder(
            adbPath,
            "shell",
            "rm",
            ROOT at DEVICE_FOLDER at DUMP_FILE
        ).start().waitFor() onError { error("Removing dump from device failed!") }

        return appDataLocalPath at LOCAL_APPLICATION_FOLDER at LOCAL_CONTENT_FOLDER at DUMP_FILE
    }

    private fun takeScreenshot(): String {
        val appDataLocalPath = System.getenv("LocalAppData")
        val adbPath = appDataLocalPath at "Android" at "Sdk" at "platform-tools" at "adb.exe"

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
            appDataLocalPath at LOCAL_APPLICATION_FOLDER at LOCAL_CONTENT_FOLDER at SCREENSHOT_FILE
        ).start().waitFor() onError { error("Pulling screenshot from device failed!") }

        ProcessBuilder(
            adbPath,
            "shell",
            "rm",
            ROOT at DEVICE_FOLDER at SCREENSHOT_FILE
        ).start().waitFor() onError { error("Removing screenshot from device failed!") }

        return appDataLocalPath at LOCAL_APPLICATION_FOLDER at LOCAL_CONTENT_FOLDER at SCREENSHOT_FILE
    }
}