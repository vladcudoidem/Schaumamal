package view

import AppViewModel
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.loadImageBitmap
import model.InspectorState
import java.io.File
import java.io.FileInputStream

@Composable
fun Screenshot(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = modifier) {
        if (viewModel.inspectorState == InspectorState.POPULATED) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    bitmap = loadImageBitmap(FileInputStream(File(viewModel.layoutData.screenshotPath))),
                    contentDescription = null,
                    modifier = Modifier
                        .graphicsLayer(
                            translationX = offset.x,
                            translationY = offset.y
                        )
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, _, _ ->
                                offset += pan
                            }
                        }
                )
            }
        }

        if (viewModel.isNodeSelected) { // implies that inspectorState == POPULATED
            Highlighter()
        }
    }
}

@Composable
fun Highlighter() {
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                color = Color.Red,
                topLeft = Offset(20f, -20f),
                size = Size(100f, 2000f),
                style = Stroke(width = 5f)
            )
        }
    }
}