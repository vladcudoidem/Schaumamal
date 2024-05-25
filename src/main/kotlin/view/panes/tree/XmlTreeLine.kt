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
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import viewmodel.Colors.highlightedBackgroundColor
import viewmodel.Colors.secondaryTextColor
import viewmodel.Dimensions.smallCornerRadius
import viewmodel.Dimensions.smallPadding
import viewmodel.Dimensions.startPaddingPerDepthLevel
import viewmodel.XmlTreeLine
import java.awt.Cursor

@Composable
fun XmlTreeLine(
    line: XmlTreeLine,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(modifier = modifier) {
        Spacer(modifier = Modifier.width(startPaddingPerDepthLevel * line.depth))

        Text(
            text = line.text,
            color = secondaryTextColor,
            modifier = Modifier
                .clip(RoundedCornerShape(smallCornerRadius))
                .background(line.textBackgroundColor)
                .clickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple(color = highlightedBackgroundColor),
                    onClick = line.onClickText
                )
                .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
                .padding(smallPadding)
        )
    }
}