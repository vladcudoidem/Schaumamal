package view.button.extraction

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import java.awt.Cursor
import shared.Colors.disabledPrimaryElementColor
import shared.Colors.extractionButtonColor
import shared.Dimensions.extractButtonDiameter
import shared.times

@Composable
fun ExtractionButton(
    isExtractButtonEnabled: Boolean,
    onExtractButtonPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val buttonColor by
        animateColorAsState(
            if (isExtractButtonEnabled) extractionButtonColor else disabledPrimaryElementColor
        )

    Button(
        onClick = onExtractButtonPressed,
        enabled = isExtractButtonEnabled,
        shape = CircleShape,
        elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
        colors =
            ButtonDefaults.buttonColors(
                backgroundColor = buttonColor,
                disabledBackgroundColor = buttonColor,
            ),
        contentPadding = PaddingValues(0.dp),
        modifier =
            modifier
                .size(extractButtonDiameter)
                .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR))),
    ) {
        Icon(
            painter = painterResource("icons/camera.svg"),
            contentDescription = null,
            tint = buttonColor * 0.3f,
            modifier = Modifier.fillMaxSize(0.7f),
        )
    }
}
