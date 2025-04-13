package shared

import androidx.compose.ui.graphics.Color

fun Color.darkenBy(percent: Float): Color {
    require(percent in 0f..1f) { "Percent parameter has to be between 0f and 1f" }
    val targetPercent = 1f - percent

    return Color(
        red = red * targetPercent,
        green = green * targetPercent,
        blue = blue * targetPercent,
    )
}
