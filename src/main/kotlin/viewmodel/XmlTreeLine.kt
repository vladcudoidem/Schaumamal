package viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString

data class XmlTreeLine(
    val text: AnnotatedString,
    val textBackgroundColor: Color,
    val depth: Int,
    val onClickText: () -> Unit
)