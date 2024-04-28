package model

import androidx.compose.ui.graphics.ImageBitmap

data class LayoutData(
    val screenshotBitmap: ImageBitmap,
    val rootNode: String // TODO change to something that makes sense
)
