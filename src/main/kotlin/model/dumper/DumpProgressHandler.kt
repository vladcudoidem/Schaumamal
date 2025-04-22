package model.dumper

import kotlin.properties.Delegates

class DumpProgressHandler(private val onProgress: (progress: Float, message: String) -> Unit) {
    private var currentProgress = 0f
        set(value) {
            field = value.coerceAtMost(1.0f)
        }

    private var screenshotCount by Delegates.notNull<Int>()
    private var takenScreenshots = 0

    fun reportStartingDump() {
        onProgress(0f, "Dumping the layout")
    }

    fun reportPreDumpSetupFinished() {
        currentProgress += 0.15f
        onProgress(currentProgress, "Dumping the layout")
    }

    fun reportXmlDumpFinished() {
        currentProgress += 0.25f
        onProgress(currentProgress, "Taking screenshot 1")
    }

    fun setExpectedScreenshotCount(count: Int) {
        screenshotCount = count
    }

    fun reportScreenshotTaken() {
        takenScreenshots++
        currentProgress += 0.6f / screenshotCount
        onProgress(
            currentProgress,
            if (takenScreenshots < screenshotCount) {
                "Taking screenshot ${takenScreenshots + 1}"
            } else {
                "Dump finished"
            },
        )
    }
}
