package view.screenshot

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import java.awt.Cursor
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.cancel
import model.parser.dataClasses.Bounds
import shared.Colors
import shared.Dimensions.Initial.maximumInitialScreenshotHeight
import shared.Dimensions.Initial.maximumInitialScreenshotWidth
import shared.Dimensions.defaultHighlighterStrokeWidth
import shared.Dimensions.largePadding
import shared.Values.minimalTouchSlop
import view.FadeVisibility
import view.UiLayoutState
import view.utils.toOffset
import view.utils.toSize

@Composable
fun ScreenshotLayer(
    uiLayoutState: UiLayoutState,
    screenshotState: ScreenshotState,
    modifier: Modifier = Modifier,
) {
    val showScreenshot by screenshotState.showScreenshot.collectAsState(initial = false)
    val imageBitmap by screenshotState.imageBitmap.collectAsState(initial = ImageBitmap(0, 0))
    val showHighlighter by screenshotState.showHighlighter.collectAsState(initial = false)
    val selectedNodeDisplayBounds by
        screenshotState.selectedNodeDisplayBounds.collectAsState(initial = Bounds.Zero)
    val screenshotLayerOffset by uiLayoutState.screenshotLayerOffset.collectAsState()
    val screenshotLayerScale by uiLayoutState.screenshotLayerScale.collectAsState()

    // This is the Box that places the next Box correctly on the screen.
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier =
            modifier
                .padding(largePadding)
                .widthIn(max = maximumInitialScreenshotWidth)
                .heightIn(max = maximumInitialScreenshotHeight),
    ) {
        // This is the Box that moves around. It inherits the size of the Image composable (or that
        // of the Canvas if it
        // is bigger, but the Canvas cannot get bigger than the Image as of now).
        Box(
            modifier =
                Modifier.graphicsLayer {
                    scaleX = screenshotLayerScale
                    scaleY = screenshotLayerScale

                    translationX = screenshotLayerOffset.x
                    translationY = screenshotLayerOffset.y
                }
        ) {
            FadeVisibility(showScreenshot) {
                WithTouchSlop(minimalTouchSlop) {
                    Screenshot(
                        bitmap = imageBitmap,
                        onSizeChanged = screenshotState::onImageSizeChanged,
                        onGesture = uiLayoutState::onImageGesture,
                        onTap = screenshotState::onImageTap,
                        onScroll = uiLayoutState::onImageScroll,
                    )
                }
            }

            val highlighterStrokeWidth = defaultHighlighterStrokeWidth / screenshotLayerScale

            FadeVisibility(showHighlighter) {
                SelectedNodeHighlighter(
                    offset = selectedNodeDisplayBounds.toOffset(),
                    size = selectedNodeDisplayBounds.toSize(),
                    strokeWidth = highlighterStrokeWidth,
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Screenshot(
    bitmap: ImageBitmap,
    onSizeChanged: (IntSize) -> Unit,
    onGesture: (Offset, Offset, Float, Float) -> Unit,
    onTap: (Offset, CoroutineContext) -> Unit,
    onScroll: (PointerEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    // The context of this coroutine scope is needed for scrolling in the view model.
    val scope = rememberCoroutineScope()
    DisposableEffect(Unit) { onDispose { scope.cancel() } }

    Image(
        bitmap = bitmap,
        contentDescription = null,
        modifier =
            modifier
                .onSizeChanged(onSizeChanged = onSizeChanged)
                .pointerInput(Unit) { detectTransformGestures(onGesture = onGesture) }
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { offset -> onTap(offset, scope.coroutineContext) })
                }
                .onPointerEvent(PointerEventType.Scroll) { event -> onScroll(event) }
                .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR))),
    )
}

@Composable
fun SelectedNodeHighlighter(
    offset: Offset,
    size: Size,
    strokeWidth: Dp,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        drawRect(
            color = Colors.selectedNodeHighlighterColor,
            topLeft = offset,
            size = size,
            style = Stroke(width = strokeWidth.toPx()),
        )
    }
}

@Composable
fun SearchResultHighlighter(
    offset: Offset,
    size: Size,
    strokeWidth: Dp,
    modifier: Modifier = Modifier,
) {
    val dashOnLength = 4.dp
    val dashOffLength = 2.dp

    Canvas(modifier = modifier) {
        drawRect(
            color = Colors.searchResultHighlighterColor,
            topLeft = offset,
            size = size,
            style =
                Stroke(
                    width = strokeWidth.toPx(),
                    pathEffect =
                        PathEffect.dashPathEffect(
                            floatArrayOf(dashOnLength.toPx(), dashOffLength.toPx())
                        ),
                ),
        )
    }
}
