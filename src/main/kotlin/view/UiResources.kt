package view

import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.shape.RoundedCornerShape
import shared.Colors.scrollbarHoverColor
import shared.Colors.scrollbarUnhoverColor
import shared.Dimensions.scrollbarThickness

val CustomScrollbarStyle = defaultScrollbarStyle().copy(
    shape = RoundedCornerShape(50),
    hoverColor = scrollbarHoverColor,
    unhoverColor = scrollbarUnhoverColor,
    thickness = scrollbarThickness
)