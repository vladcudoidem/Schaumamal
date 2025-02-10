package view.panes.tree

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontFamily
import shared.Colors.accentColor
import shared.Colors.primaryTextColor
import shared.Dimensions.smallCornerRadius
import shared.Dimensions.smallPadding
import shared.Dimensions.startPaddingPerDepthLevel
import view.panes.XmlTreeLine
import java.awt.Cursor

@Composable
fun XmlTreeLine(
    line: XmlTreeLine,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val indication = ripple(color = accentColor)

    Row(modifier = modifier) {
        Spacer(modifier = Modifier.width(startPaddingPerDepthLevel * line.depth))

        Text(
            text = line.text,
            color = primaryTextColor,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier
                .clip(RoundedCornerShape(smallCornerRadius))
                .background(line.textBackgroundColor)
                .clickable(
                    interactionSource = interactionSource,
                    indication = indication,
                    onClick = line.onClickText
                )
                .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
                .padding(smallPadding)
        )
    }
}