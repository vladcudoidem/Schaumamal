package view.button.extraction

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import java.awt.Cursor
import shared.Colors.extractionButtonColor
import shared.Dimensions.extractButtonDiameter

@Composable
fun ExtractionButton(
    isExtractButtonEnabled: Boolean,
    onExtractButtonPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val disabledBackgroundColor by
        animateColorAsState(
            if (isExtractButtonEnabled) extractionButtonColor
            else extractionButtonColor.copy(alpha = 0.3f)
        )

    Button(
        onClick = onExtractButtonPressed,
        enabled = isExtractButtonEnabled,
        shape = CircleShape,
        colors =
            ButtonDefaults.buttonColors(
                backgroundColor = extractionButtonColor,
                disabledBackgroundColor = disabledBackgroundColor,
            ),
        modifier =
            modifier
                .size(extractButtonDiameter)
                .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR))),
    ) {}
}
