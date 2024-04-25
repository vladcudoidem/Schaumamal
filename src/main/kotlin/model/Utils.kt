package model

object Utils {
    fun dumpXml() {
        val dumpCommand = "adb shell uiautomator dump --windows /sdcard/ui_dump.xml"
        Runtime.getRuntime().exec(dumpCommand).waitFor()

        val pullCommand = "adb pull /sdcard/ui_dump.xml ./dump/ui_dump.xml"
        Runtime.getRuntime().exec(pullCommand).waitFor()
    }

    fun takeScreenshot() {
        Runtime.getRuntime().exec("adb shell screencap /sdcard/screenshot.png")

        // Pulling the screenshot file to the local machine
        Runtime.getRuntime().exec("adb pull /sdcard/screenshot.png ./screenshot/screenshot.png")

        // Optionally, remove the screenshot from the device
//        Runtime.getRuntime().exec("adb shell rm /sdcard/screenshot.png")
    }
}