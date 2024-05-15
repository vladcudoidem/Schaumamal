package shared.graphics

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

// TODO use later in view model

data class ImageGraphics(
    override val offset: Offset,
    override val size: Size
): Graphics
