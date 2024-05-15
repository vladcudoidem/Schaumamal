package view.screenshot

import AppViewModel
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import java.awt.Cursor

@Composable
fun ScreenshotLayer(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    Box(modifier = modifier) {
        if (viewModel.showImage) {
            Screenshot()
        }

        if (viewModel.showHighlighter) {
            HighlighterCanvas(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun Screenshot(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current
    val coroutineScope = rememberCoroutineScope()
        // The context of this coroutine scope is needed for scrolling in the view model.

    Image(
        bitmap = viewModel.imageBitmap,
        contentDescription = null,
        modifier = modifier
            .graphicsLayer(
                translationX = viewModel.imageComposableGraphics.offset.x,
                translationY = viewModel.imageComposableGraphics.offset.y
            )
            .pointerInput(Unit) {
                detectTransformGestures(onGesture = viewModel::onImageGesture)
            }
            .onSizeChanged(onSizeChanged = viewModel::onImageSizeChanged)
            .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        viewModel.onImageTap(
                            offset = offset,
                            uiCoroutineContext = coroutineScope.coroutineContext
                        )
                    }
                )
            }
    )
}

@Composable
fun HighlighterCanvas(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    Canvas(modifier = modifier) {
        val (highlighterOffset, highlighterSize) = viewModel.highlighterGraphics
        drawRect(
            color = Color.Red,
            topLeft = highlighterOffset,
            size = highlighterSize,
            style = Stroke(width = 5f)
        )
    }
}
