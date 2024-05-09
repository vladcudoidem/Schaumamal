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
import androidx.compose.ui.unit.dp
import model.InspectorState
import view.Colors
import view.Dimensions
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
            onClick = { viewModel.extractLayout() },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(backgroundColor = Colors.buttonColor),
            modifier = Modifier
                .size(40.dp)
                .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
        ) { }

        if (viewModel.inspectorState == InspectorState.EMPTY) {
            SpacerAndText(text = "...smash the red button")
        } else if (viewModel.inspectorState == InspectorState.WAITING) {
            SpacerAndText(text = "...dumping")
        }
    }
}

@Composable
fun SpacerAndText(text: String, modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.width(Dimensions.largePadding))
    Text(
        text = text,
        color = Colors.hintTextColor,
        fontFamily = FontFamily.Monospace
    )
}