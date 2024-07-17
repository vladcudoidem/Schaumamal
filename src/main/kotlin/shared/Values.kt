package shared

import oldModel.on

@Suppress("MayBeConstant", "unused")
object Values {

    /* Screenshot Layer */

    // Zoom
    val scrollZoomFactor = on(win = 10, mac = 50, lin = 50)
    val keyboardZoomFactor = 1.1f
    val minScreenshotScale = 0.1f
    val maxScreenshotScale = 20f

    // Screenshot
    val screenshotLayerWidthPercentage = 0.6f
    val minimalTouchSlop = 5f
}