package view.panes.tree

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import java.awt.Cursor
import shared.Colors.accentColor
import shared.Colors.discreteTextColor
import shared.Colors.primaryTextColor
import shared.Dimensions.smallCornerRadius
import shared.Dimensions.smallPadding
import shared.Dimensions.startPaddingPerDepthLevel
import view.panes.XmlTreeLine

@Composable
fun XmlTreeLine(line: XmlTreeLine, modifier: Modifier = Modifier) {
    val textInteractionSource = remember { MutableInteractionSource() }
    val textIndication = ripple(color = accentColor)

    val arrowInteractionSource = remember { MutableInteractionSource() }
    val arrowIndication = ripple(color = accentColor)

    val isArrowEnabled = line.hasChildren

    val isCollapsed by line.isCollapsed.collectAsState()
    val isSelected by line.isSelected.collectAsState()

    val lineHeight = 27.dp

    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Spacer(modifier = Modifier.width(startPaddingPerDepthLevel * line.depth))

        val arrowColor: Color
        if (isArrowEnabled) {
            arrowColor = primaryTextColor
        } else {
            arrowColor = discreteTextColor.copy(alpha = 0.6f)
        }

        val onClickArrow: () -> Unit
        val arrowResourcePath: String
        val arrowPaddingValues: PaddingValues
        if (!isCollapsed) {
            onClickArrow = { line.collapse() }
            arrowResourcePath = "icons/arrow_down.svg"
            arrowPaddingValues = PaddingValues(top = 1.dp)
        } else {
            onClickArrow = { line.expand() }
            arrowResourcePath = "icons/arrow_right.svg"
            arrowPaddingValues = PaddingValues(start = 1.dp)
        }

        Box(
            modifier =
                Modifier.clip(RoundedCornerShape(smallCornerRadius))
                    .background(Color.Transparent)
                    .clickable(
                        interactionSource = arrowInteractionSource,
                        indication = arrowIndication,
                        onClick = { onClickArrow() },
                        enabled = isArrowEnabled,
                    )
                    .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
                    .size(lineHeight),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(arrowResourcePath),
                contentDescription = null,
                tint = arrowColor,
                modifier = Modifier.fillMaxSize(0.8f).padding(arrowPaddingValues),
            )
        }

        val textBackgroundColor =
            if (isSelected) {
                accentColor
            } else {
                Color.Transparent
            }

        Text(
            text = line.text,
            color = primaryTextColor,
            modifier =
                Modifier.clip(RoundedCornerShape(smallCornerRadius))
                    .background(textBackgroundColor)
                    .clickable(
                        interactionSource = textInteractionSource,
                        indication = textIndication,
                        onClick = line.onClickText,
                    )
                    .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
                    .height(lineHeight)
                    .padding(
                        top = smallPadding,
                        start = smallPadding,
                        end = smallPadding,
                        bottom = smallPadding + 1.dp,
                    ),
        )
    }
}
