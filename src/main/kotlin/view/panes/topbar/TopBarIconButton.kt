package view.panes.topbar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import java.awt.Cursor
import shared.Colors.disabledPrimaryElementColor
import shared.Colors.primaryElementColor
import shared.Dimensions.defaultTopBarContentHeight
import shared.Dimensions.largeCornerRadius

val topBarButtonSize = defaultTopBarContentHeight

@Composable
fun TopBarIconButton(
    iconResource: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    buttonColors: ButtonColors =
        ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            contentColor = primaryElementColor,
            disabledBackgroundColor = Color.Transparent,
            disabledContentColor = disabledPrimaryElementColor,
        ),
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(largeCornerRadius),
        contentPadding = PaddingValues(0.dp),
        colors = buttonColors,
        elevation =
            ButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                hoveredElevation = 0.dp,
                focusedElevation = 0.dp,
            ),
        modifier =
            Modifier.size(topBarButtonSize)
                .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR))),
    ) {
        Icon(
            painter = painterResource(iconResource),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(0.55f),
        )
    }
}
