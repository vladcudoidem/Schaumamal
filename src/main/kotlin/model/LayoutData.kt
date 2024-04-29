package model

import androidx.compose.ui.graphics.ImageBitmap
import model.parser.SystemNode

data class LayoutData(
    val screenshot: ImageBitmap,
    val root: SystemNode
)
