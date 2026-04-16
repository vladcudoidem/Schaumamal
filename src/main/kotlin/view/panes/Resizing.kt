package view.panes

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import java.awt.Cursor
import org.jetbrains.jewel.ui.component.painterResource
import shared.Colors
import shared.Dimensions

@Composable
fun ResizingHandle(
    onDrag: (PointerInputChange, Offset, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(Dimensions.handleDiameter)
                .clip(CircleShape)
                .background(Color.Transparent)
                .pointerHoverIcon(PointerIcon(Cursor(Cursor.CROSSHAIR_CURSOR)))
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount -> onDrag(change, dragAmount, density) }
                },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource("icons/move.svg"),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            tint = Colors.resizingHandleColor,
        )
    }
}

@Composable
fun ResizingArea(
    pointerIcon: PointerIcon,
    onDrag: (PointerInputChange, Offset, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier.pointerHoverIcon(pointerIcon).pointerInput(Unit) {
                detectDragGestures { change, dragAmount -> onDrag(change, dragAmount, density) }
            }
    )
}
