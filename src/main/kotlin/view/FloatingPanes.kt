package view

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import java.awt.Cursor

@Composable
fun FloatingPanes(modifier: Modifier = Modifier) {
    val density = LocalDensity.current.density
    var upperBoxHeight by remember { mutableStateOf(300.dp) }
    var lowerBoxHeightPx by remember { mutableStateOf(0.dp) }
    var wholeBoxWidth by remember { mutableStateOf(250.dp) }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(wholeBoxWidth)
            .padding(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)

        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(30.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Colors.wedgeColor)
                    .pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            if (change.positionChange() != Offset.Zero) {
                                change.consume()
                            }
                            wholeBoxWidth = (wholeBoxWidth - dragAmount.x.dp / 2).coerceAtLeast(100.dp)
                        }
                    }
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(upperBoxHeight)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Colors.floatingPaneBackgroundColor)
                        .padding(10.dp)
                ) {
                    // ...
                }

                Box(
                    modifier = Modifier
                        .width(30.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Colors.wedgeColor)
                        .pointerHoverIcon(PointerIcon(Cursor(Cursor.S_RESIZE_CURSOR)))
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                if (change.positionChange() != Offset.Zero) {
                                    change.consume()
                                }
                                if (
                                    dragAmount.y.dp > 0.dp && lowerBoxHeightPx > 100.dp ||
                                    dragAmount.y.dp < 0.dp
                                ) {
                                    upperBoxHeight = (upperBoxHeight + dragAmount.y.dp / 2).coerceAtLeast(100.dp)
                                    // TODO what is with the '/ 2' factor?
                                }
                            }
                        }
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Colors.floatingPaneBackgroundColor)
                        .padding(10.dp)
                        .onSizeChanged {
                            lowerBoxHeightPx = (it.height / density).dp
                        }
                ) {
                    // ...
                }
            }
        }
    }
}