package view.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import shared.Colors.elevatedBackgroundColor
import shared.Colors.primaryTextColor

@Composable
fun DisplayControlPill(modifier: Modifier = Modifier) {
    HorizontalPill(modifier = modifier) {
        RoundIconButton(
            onClick = { },
            iconPainter = painterResource("icons/arrow_backward.svg"),
            iconModifier = Modifier.padding(end = 2.dp)
        )

        Text(
            text = "Display",
            color = primaryTextColor,
            fontFamily = FontFamily.SansSerif
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(primaryTextColor)
                .size(height = 25.dp, width = 40.dp)
        ) {
            Text(
                text = "2/3",
                color = elevatedBackgroundColor,
                fontFamily = FontFamily.SansSerif
            )
        }

        RoundIconButton(
            onClick = { },
            iconPainter = painterResource("icons/arrow_forward.svg"),
            iconModifier = Modifier.padding(start = 2.dp)
        )
    }
}