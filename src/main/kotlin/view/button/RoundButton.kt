package view.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import shared.Colors.backgroundColor
import shared.Colors.primaryElementColor
import shared.Dimensions.extractButtonDiameter
import java.awt.Cursor

@Composable
fun RoundIconButton(
    onClick: () -> Unit,
    iconPainter: Painter,
    buttonModifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    enabled: Boolean = true,
    buttonColors: ButtonColors = ButtonDefaults.buttonColors(
        contentColor = backgroundColor,
        disabledContentColor = backgroundColor.copy(alpha = 0.5f),
        backgroundColor = primaryElementColor,
        disabledBackgroundColor = primaryElementColor
    )
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        colors = buttonColors,
        modifier = buttonModifier
            .size(extractButtonDiameter)
            .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
    ) {
        Icon(
            painter = iconPainter,
            contentDescription = null,
            modifier = iconModifier.fillMaxSize(0.6f)
        )
    }
}