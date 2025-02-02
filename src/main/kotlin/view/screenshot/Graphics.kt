package view.screenshot

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

data class Graphics (
    val offset: Offset,
    val size: Size
) {
    companion object {
        val Unspecified = Graphics(offset = Offset.Unspecified, size = Size.Unspecified)
    }
}