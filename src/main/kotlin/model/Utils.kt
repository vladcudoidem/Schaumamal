package model

object Utils {
    fun dumpXml() {
        val dumpCommand = "adb shell uiautomator dump --windows /sdcard/ui_dump.xml"
        Runtime.getRuntime().exec(dumpCommand).waitFor()

        val pullCommand = "adb pull /sdcard/ui_dump.xml ./dump/ui_dump.xml"
        Runtime.getRuntime().exec(pullCommand).waitFor()
    }
}