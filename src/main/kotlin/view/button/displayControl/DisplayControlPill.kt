package view.button.displayControl

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import shared.Colors
import view.button.HorizontalPill
import view.button.RoundIconButton

@Composable
fun DisplayControlPill(modifier: Modifier = Modifier) {
    HorizontalPill(modifier = modifier) {
        RoundIconButton(
            onClick = { },
            iconPainter = painterResource("icons/arrow_backward.svg"),
            iconModifier = Modifier.padding(end = 2.dp)
        )

        Text(
            text = "Display 1/3",
            color = Colors.primaryTextColor,
            fontFamily = FontFamily.Monospace
        )

        RoundIconButton(
            onClick = { },
            iconPainter = painterResource("icons/arrow_forward.svg"),
            iconModifier = Modifier.padding(start = 2.dp)
        )
    }
}