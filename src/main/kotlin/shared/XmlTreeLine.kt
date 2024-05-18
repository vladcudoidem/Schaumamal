package shared

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates

data class XmlTreeLine(
    val text: String,
    val textBackgroundColor: Color,
    val depth: Int,
    val onClickText: () -> Unit,
    val onTreeLineGloballyPositioned: (LayoutCoordinates) -> Unit
)