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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import shared.Colors.accentColor
import shared.Colors.primaryTextColor
import shared.Dimensions.maximumPropertyValueWidth
import shared.Dimensions.propertyNameWidth
import shared.Dimensions.smallCornerRadius
import shared.Dimensions.smallPadding
import view.FadeVisibility
import java.awt.Cursor

@Composable
fun PropertyRow(
    property: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val clipboard = LocalClipboardManager.current

    val interactionSource = remember { MutableInteractionSource() }
    val indication = rememberRipple(color = accentColor)

    val scope = rememberCoroutineScope()
    DisposableEffect(Unit) {
        onDispose {
            scope.cancel()
        }
    }

    var copyConfirmationVisible by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(start = smallPadding)
    ) {
        Text(
            text = property,
            fontFamily = FontFamily.SansSerif,
            color = primaryTextColor,
            modifier = Modifier.width(propertyNameWidth)
        )

        Text(
            text = value,
            color = primaryTextColor,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier
                .widthIn(max = maximumPropertyValueWidth)
                .clip(RoundedCornerShape(smallCornerRadius))
                .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
                // It would make little sense to handle this in the view model as the behaviour is always the same.
                .clickable(interactionSource, indication) {
                    // Copy value to clipboard.
                    clipboard.setText(AnnotatedString(value))

                    // Show confirmation text.
                    copyConfirmationVisible = true
                    scope.launch {
                        delay(timeMillis = 1500)
                        copyConfirmationVisible = false
                    }
                }
                .padding(smallPadding)
        )

        FadeVisibility(copyConfirmationVisible) {
            Text(
                text = "✓ copied",
                color = accentColor,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}