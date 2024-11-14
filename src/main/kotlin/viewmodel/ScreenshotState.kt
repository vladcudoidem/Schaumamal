package viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import oldModel.InspectorState
import oldModel.parser.xmlElements.Node
import oldModel.parser.xmlElements.System
import shared.Dimensions.defaultHighlighterStrokeWidth
import shared.Dimensions.extractButtonDiameter
import shared.Dimensions.largePadding
import shared.Dimensions.mediumPadding
import shared.Dimensions.smallPadding
import shared.Dimensions.wedgeSmallDimension
import shared.Values.keyboardZoomFactor
import shared.Values.maxScreenshotScale
import shared.Values.minScreenshotScale
import shared.Values.scrollZoomFactor
import viewmodel.extraUiLogic.extractDisplayGraphics
import viewmodel.extraUiLogic.forFirstNodeUnder
import viewmodel.extraUiLogic.getNodesOrderedByDepth
import java.io.File
import java.io.FileInputStream
import kotlin.coroutines.CoroutineContext
import kotlin.math.min

class ScreenshotState(
    private val getInspectorState: () -> InspectorState,
    private val getScreenshotFile: () -> File,
    private val getDataRoot: () -> System,
    private val isNodeSelected: () -> Boolean,
    private val getSelectedNode: () -> Node,
    private val selectNode: (Node) -> Unit,
    private val getDensity: () -> Float,
    private val getPaneWidth: () -> Dp,
    private val getPanesConstraint: () -> DpSize,
    private val scrollToSelectedNode: (CoroutineContext) -> Unit
) {
    val showScreenshot get() = getInspectorState() == InspectorState.POPULATED
    val imageBitmap by derivedStateOf {
        loadImageBitmap(FileInputStream(getScreenshotFile())).apply {
            // Store the screenshot file size as soon as possible.
            screenshotFileSize = Size(height = height.toFloat(), width = width.toFloat())
        }
    }

    // This does not to be a state as it does not have to trigger any recomposition. It is initialized when the model
    // updates the data and thus the imageBitmap.
    private var screenshotFileSize = Size.Unspecified

    var screenshotLayerOffset by mutableStateOf(Offset.Zero)
    var screenshotLayerScale by mutableStateOf(1f)

    // size of the image composable
    private var screenshotComposableSize by mutableStateOf(Size.Unspecified)

    // It is irrelevant whether we use width or height when calculating the conversion factor.
    private val displayPixelConversionFactor
        get() = screenshotComposableSize.height / screenshotFileSize.height

    val showHighlighter get() = showScreenshot && isNodeSelected()
    val highlighterOffset by derivedStateOf { selectedNodeDisplayGraphics.offset }
    val highlighterSize by derivedStateOf { selectedNodeDisplayGraphics.size }
    private val selectedNodeDisplayGraphics
        get() = getSelectedNode().extractDisplayGraphics(displayPixelConversionFactor)

    val highlighterStrokeWidth get() = (defaultHighlighterStrokeWidth / screenshotLayerScale).toPx(getDensity())

    @Suppress("UNUSED_PARAMETER")
    fun onImageGesture(centroid: Offset, pan: Offset, zoom: Float, rotation: Float) {
        screenshotLayerOffset += pan * screenshotLayerScale
    }

    fun onImageSizeChanged(size: IntSize) {
        screenshotComposableSize = size.toSize()
    }

    fun onImageTap(offset: Offset, uiCoroutineContext: CoroutineContext) {
        // Extract node list with the first nodes being the deepest ones.
        val flatNodeListByDepth = getDataRoot().getNodesOrderedByDepth(deepNodesFirst = true)

        flatNodeListByDepth.forFirstNodeUnder(
            offset = offset,
            displayPixelConversionFactor = displayPixelConversionFactor
        ) { matchingNode ->
            // Update the selected node if a matching node was found.
            selectNode(matchingNode)

            scrollToSelectedNode(uiCoroutineContext)
        }
    }

    fun onImageScroll(event: PointerEvent) {
        val oldScale = screenshotLayerScale
        val change = event.changes.first()
        val zoomFactor = 1f - change.scrollDelta.y / scrollZoomFactor // We are using an arbitrary factor.

        // First change scale.
        val newScale = screenshotLayerScale * zoomFactor

        // Don't change scale if it will be outside limits.
        if (newScale !in minScreenshotScale..maxScreenshotScale) {
            return
        } else {
            screenshotLayerScale = newScale
        }

        // Change offset. Enables zooming around the pointer location.
        val pointerOffsetFromCenter =
            change.position - Offset(x = screenshotComposableSize.width, y = screenshotComposableSize.height) / 2f
        screenshotLayerOffset -= pointerOffsetFromCenter * oldScale * (zoomFactor - 1f)
    }

    // This is a method that is highly dependent on the specific arrangement of the UI components on the screen. It will
    // likely break when the UI undergoes significant change.
    fun onFitScreenshotToScreenButtonPressed() {
        // The following variables define the screenshot area (the area between the buttons and the panes).
        val topVisibleScreenshotAreaBound = mediumPadding + extractButtonDiameter
        val bottomVisibleScreenshotAreaBound = getPanesConstraint().height
        val startVisibleScreenshotAreaBound = mediumPadding + extractButtonDiameter
        val endVisibleScreenshotAreaBound =
            getPanesConstraint().width - (wedgeSmallDimension + smallPadding + getPaneWidth() + mediumPadding)

        // We first place the screenshot in the center of the screenshot area.

        // The reference of this offset is the upper-left corner (x = 0, y = 0).
        val screenshotComposableGlobalOffsetAtCenter = Offset(
            x = largePadding.toPx(getDensity()) + screenshotComposableSize.width / 2,
            y = getPanesConstraint().height.toPx(getDensity()) / 2
        )

        // The reference of this offset is the upper-left corner (x = 0, y = 0) as well.
        val targetGlobalOffsetAtCenter = Offset(
            x = (startVisibleScreenshotAreaBound + endVisibleScreenshotAreaBound).toPx(getDensity()) / 2,
            y = (topVisibleScreenshotAreaBound + bottomVisibleScreenshotAreaBound).toPx(getDensity()) / 2
        )

        screenshotLayerOffset = targetGlobalOffsetAtCenter - screenshotComposableGlobalOffsetAtCenter

        // Then we resize the screenshot to take up as much space as possible (the actual "fit-to-screen").

        val targetHorizontalScale =
            (endVisibleScreenshotAreaBound - startVisibleScreenshotAreaBound)
                .toPx(getDensity())
                .div(screenshotComposableSize.width)
        val targetVerticalScale =
            (bottomVisibleScreenshotAreaBound - topVisibleScreenshotAreaBound)
                .toPx(getDensity())
                .div(screenshotComposableSize.height)

        // We use this factor to leave some space between the screenshot and the other UI elements.
        val convenienceFactor = 0.9f

        // We then use the most restrictive scale.
        screenshotLayerScale =
            min(targetHorizontalScale, targetVerticalScale)
                .times(convenienceFactor)
                .coerceIn(
                    minimumValue = minScreenshotScale,
                    maximumValue = maxScreenshotScale
                )
    }

    fun onEnlargeScreenshotButtonPressed() {
        screenshotLayerScale = (screenshotLayerScale * keyboardZoomFactor).coerceAtMost(maxScreenshotScale)
    }

    fun onShrinkScreenshotButtonPressed() {
        screenshotLayerScale = (screenshotLayerScale / keyboardZoomFactor).coerceAtLeast(minScreenshotScale)
    }
}
