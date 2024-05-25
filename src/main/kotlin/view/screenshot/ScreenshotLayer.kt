package view.screenshot

import AppViewModel
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import shared.Dimensions.largePadding
import shared.Values.screenshotLayerWidthPercentage
import java.awt.Cursor

@Composable
fun ScreenshotLayer(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current
    // The context of this coroutine scope is needed for scrolling in the view model.
    val coroutineScope = rememberCoroutineScope()

    // This is the Box that places the next Box correctly on the screen.
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
            .padding(largePadding)
            .fillMaxHeight()
            .fillMaxWidth(screenshotLayerWidthPercentage)
    ) {
        // This is the Box that moves around. It inherits the size of the Image composable (or that of the Canvas if it
        // is bigger, but the Canvas cannot get bigger than the Image as of now).
        Box(
            modifier = Modifier
                .onSizeChanged(onSizeChanged = viewModel::onImageSizeChanged)
                .graphicsLayer {
                    scaleX = viewModel.screenshotLayerScale
                    scaleY = viewModel.screenshotLayerScale

                    translationX = viewModel.screenshotLayerOffset.x
                    translationY = viewModel.screenshotLayerOffset.y
                }
                .pointerInput(Unit) {
                    detectTransformGestures(onGesture = viewModel::onImageGesture)
                }
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
                .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
        ) {
            if (viewModel.showScreenshot) {
                Screenshot()
            }

            if (viewModel.showHighlighter) {
                Highlighter()
            }
        }
    }
}

@Composable
fun Screenshot(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    Image(
        bitmap = viewModel.imageBitmap,
        contentDescription = null,
        modifier = modifier
    )
}

@Composable
fun Highlighter(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    Canvas(modifier = modifier) {
        drawRect(
            color = Color.Red,
            topLeft = viewModel.highlighterOffset,
            size = viewModel.highlighterSize,
            style = Stroke(width = viewModel.highlighterStrokeWidth)
        )
    }
}