package view.button.displayControl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import shared.Colors.elevatedBackgroundColor
import shared.Colors.primaryTextColor
import shared.Dimensions.smallPadding
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
            text = "Display",
            color = primaryTextColor,
            fontFamily = FontFamily.SansSerif
        )

        Text(
            text = "1/3",
            color = elevatedBackgroundColor,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(primaryTextColor)
                .padding(smallPadding)
        )

        RoundIconButton(
            onClick = { },
            iconPainter = painterResource("icons/arrow_forward.svg"),
            iconModifier = Modifier.padding(start = 2.dp)
        )
    }
}