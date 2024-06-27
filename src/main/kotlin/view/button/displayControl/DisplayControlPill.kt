package view.button.displayControl

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontFamily
import shared.Colors
import shared.Colors.wedgeColor
import shared.Dimensions.extractButtonDiameter
import view.button.HorizontalPill
import java.awt.Cursor

@Composable
fun DisplayControlPill(modifier: Modifier = Modifier) {
    // Todo: remove duplicate code.
    HorizontalPill {
        Button(
            onClick = { },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(backgroundColor = wedgeColor),
            modifier = modifier
                .size(extractButtonDiameter)
                .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
        ) {
            Text("<")
        }

        Text(
            text = "Display 1/3",
            color = Colors.primaryTextColor,
            fontFamily = FontFamily.Monospace
        )

        Button(
            onClick = { },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(backgroundColor = wedgeColor),
            modifier = modifier
                .size(extractButtonDiameter)
                .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
        ) {
            Text(">")
        }
    }
}