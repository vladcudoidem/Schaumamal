package viewmodel

import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.shape.RoundedCornerShape
import viewmodel.Colors.scrollbarHoverColor
import viewmodel.Colors.scrollbarUnhoverColor
import viewmodel.Dimensions.scrollbarThickness

val CustomScrollbarStyle = defaultScrollbarStyle().copy(
    shape = RoundedCornerShape(50),
    hoverColor = scrollbarHoverColor,
    unhoverColor = scrollbarUnhoverColor,
    thickness = scrollbarThickness
)