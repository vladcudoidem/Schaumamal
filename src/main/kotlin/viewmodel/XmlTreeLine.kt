package viewmodel

import androidx.compose.ui.graphics.Color

data class XmlTreeLine(
    val text: String,
    val textBackgroundColor: Color,
    val depth: Int,
    val onClickText: () -> Unit
)