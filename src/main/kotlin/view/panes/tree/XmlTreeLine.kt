package view.panes.tree

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onGloballyPositioned
import shared.XmlTreeLine
import view.Colors.secondaryTextColor
import view.Dimensions.smallCornerRadius
import view.Dimensions.smallPadding
import view.Dimensions.startPaddingPerDepthLevel
import java.awt.Cursor

@Composable
fun XmlTreeLine(
    line: XmlTreeLine,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.onGloballyPositioned(line.onTreeLineGloballyPositioned)
    ) {
        Spacer(modifier = Modifier.width(startPaddingPerDepthLevel * line.depth))

        Text(
            text = line.text,
            color = secondaryTextColor,
            modifier = Modifier
                .clip(RoundedCornerShape(smallCornerRadius))
                .background(line.textBackgroundColor)
                .clickable(onClick = line.onClickText)
                .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
                .padding(smallPadding)
        )
    }
}