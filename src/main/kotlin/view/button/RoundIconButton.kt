package view.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import java.awt.Cursor
import shared.Colors.disabledPrimaryElementColor
import shared.Colors.elevatedBackgroundColor
import shared.Colors.primaryElementColor
import shared.Dimensions.extractButtonDiameter

@Composable
fun RoundIconButton(
    onClick: () -> Unit,
    iconPainter: Painter,
    buttonModifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    enabled: Boolean = true,
    buttonColors: ButtonColors =
        ButtonDefaults.buttonColors(
            contentColor = primaryElementColor,
            disabledContentColor = disabledPrimaryElementColor,
            backgroundColor = elevatedBackgroundColor,
            disabledBackgroundColor = elevatedBackgroundColor,
        ),
    buttonElevation: ButtonElevation =
        ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp,
            hoveredElevation = 0.dp,
            focusedElevation = 0.dp,
        ),
    backgroundBrush: Brush? = null,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        colors = buttonColors,
        elevation = buttonElevation,
        modifier =
            buttonModifier
                .size(extractButtonDiameter)
                .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR))),
    ) {
        Box(
            modifier =
                Modifier.fillMaxSize()
                    .background(
                        brush =
                            backgroundBrush
                                ?: SolidColor(buttonColors.backgroundColor(enabled).value),
                        shape = CircleShape,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = iconPainter,
                contentDescription = null,
                modifier = iconModifier.fillMaxSize(0.6f),
            )
        }
    }
}
