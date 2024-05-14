package view.button

import AppViewModel
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontFamily
import view.Colors.buttonColor
import view.Colors.hintTextColor
import view.Dimensions.extractButtonDiameter
import view.Dimensions.largePadding
import view.Dimensions.mediumPadding
import java.awt.Cursor

@Composable
fun RoundButton(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier.padding(mediumPadding)
    ) {
        Button(
            onClick = { viewModel.onExtractButtonPressed() },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
            modifier = Modifier
                .size(extractButtonDiameter)
                .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
        ) { }

        if (viewModel.showButtonText) {
            Spacer(modifier = Modifier.width(largePadding))
            Text(
                text = viewModel.buttonText,
                color = hintTextColor,
                fontFamily = FontFamily.Monospace,
                modifier = modifier
            )
        }
    }
}
