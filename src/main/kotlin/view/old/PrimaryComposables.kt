package view.old

import AppViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun Toolbar(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ToolbarButton()
    }
}

@Composable
fun ScreenshotBox(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(5.dp))
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        if (viewModel.isInspectorPopulated) {
            var scale by remember { mutableStateOf(1f) }
            var offset by remember { mutableStateOf(Offset.Zero) }

            Box(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    offset += Offset(dragAmount.x, dragAmount.y)
                }
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.type == PointerEventType.Scroll) {
                            event.changes.forEach { pointerInputChange ->
                                val scrollY = pointerInputChange.scrollDelta.y
                                scale *= (1 + scrollY * 0.01).toFloat() // Adjust the 0.01 factor to control zoom sensitivity
                            }
                        }
                    }
                }
            }) {
                Image(
                    bitmap = viewModel.layoutData.screenshot,
                    contentDescription = "Resizable and draggable image",
                    modifier = Modifier.graphicsLayer(
                        scaleX = maxOf(0.1f, scale), // Prevent scale from going too low
                        scaleY = maxOf(0.1f, scale),
                        translationX = offset.x,
                        translationY = offset.y
                    )
                )
            }
        } else {
            Text("No data...")
        }
    }
}

@Composable
fun UiTreeBox(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(5.dp))
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        if (viewModel.isInspectorPopulated){
            val scrollState = rememberScrollState()

            Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
                LayoutPrinter.getStructure(viewModel.layoutData.root)
            }
        } else {
            Text("No data...")
        }
    }
}