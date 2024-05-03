package view

import AppViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
}