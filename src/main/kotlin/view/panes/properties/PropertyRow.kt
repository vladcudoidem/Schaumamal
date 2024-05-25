package view.panes.properties

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import viewmodel.Colors.highlightedBackgroundColor
import viewmodel.Colors.secondaryTextColor
import viewmodel.Dimensions.maximumPropertyValueWidth
import viewmodel.Dimensions.propertyNameWidth
import viewmodel.Dimensions.smallCornerRadius
import viewmodel.Dimensions.smallPadding
import java.awt.Cursor

@Composable
fun PropertyRow(
    property: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val clipboard = LocalClipboardManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val indication = rememberRipple(color = highlightedBackgroundColor)

    Row(modifier = modifier) {
        Text(
            text = property,
            color = secondaryTextColor,
            modifier = Modifier.width(propertyNameWidth)
        )

        Text(
            text = value,
            color = secondaryTextColor,
            modifier = Modifier
                .widthIn(max = maximumPropertyValueWidth)
                .clip(RoundedCornerShape(smallCornerRadius))
                .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
                .clickable(interactionSource, indication) { clipboard.setText(AnnotatedString(value)) }
                    // It would make little sense to handle this in the view model as the behaviour is always the same.
                .padding(smallPadding)
        )
    }
}