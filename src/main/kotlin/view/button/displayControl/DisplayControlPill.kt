package view.button.displayControl

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
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
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = wedgeColor),
            modifier = modifier
                .size(extractButtonDiameter)
                .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
        ) {
            Icon(
                painter = painterResource("symbolIcons/arrow_backward.svg"),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(0.8f)
                    .padding(end = 2.dp)
            )
        }

        Text(
            text = "Display 1/3",
            color = Colors.primaryTextColor,
            fontFamily = FontFamily.Monospace
        )

        Button(
            onClick = { },
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = wedgeColor),
            modifier = modifier
                .size(extractButtonDiameter)
                .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
        ) {
            Icon(
                painter = painterResource("symbolIcons/arrow_forward.svg"),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(0.8f)
                    .padding(start = 2.dp)
            )
        }
    }
}