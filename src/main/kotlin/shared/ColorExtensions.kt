package shared

import androidx.compose.ui.graphics.Color

operator fun Color.times(factor: Float): Color =
    copy(red = red * factor, green = green * factor, blue = blue * factor)
