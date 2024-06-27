package view.button.extraction

import AppViewModel
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
import shared.Colors.extractionButtonColor
import shared.Dimensions.extractButtonDiameter
import java.awt.Cursor

@Composable
fun ExtractionButton(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    val disabledBackgroundColor by animateColorAsState(
        if (viewModel.isButtonEnabled) extractionButtonColor else extractionButtonColor.copy(alpha = 0.3f)
    )

    Button(
        onClick = viewModel::onExtractButtonPressed,
        enabled = viewModel.isButtonEnabled,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = extractionButtonColor,
            disabledBackgroundColor = disabledBackgroundColor
        ),
        modifier = modifier
            .size(extractButtonDiameter)
            .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
    ) { }
}