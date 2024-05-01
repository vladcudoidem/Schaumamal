package view

import AppViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import view.Dimensions.mediumPadding
import java.awt.Cursor

@Composable
fun RoundButton(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    Box(modifier = modifier.padding(mediumPadding)) {
        Button(
            onClick = { viewModel.extractLayout() },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(backgroundColor = Colors.buttonColor),
            modifier = Modifier
                .size(40.dp)
                .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
        ) { }
    }
}