package viewmodel

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import model.CoroutineManager
import model.InspectorState
import model.LayoutInspector
import model.extractionManagers.getExtractionManager
import model.parser.xmlElements.Node
import shared.Dimensions.Initial.initialPaneWidth
import shared.Dimensions.Initial.initialUpperPaneHeight
import shared.Dimensions.defaultHighlighterStrokeWidth
import shared.Dimensions.extractButtonDiameter
import shared.Dimensions.largePadding
import shared.Dimensions.mediumPadding
import shared.Dimensions.minimumPaneDimension
import shared.Dimensions.smallPadding
import shared.Dimensions.wedgeSmallDimension
import shared.Values.keyboardZoomFactor
import shared.Values.maxScreenshotScale
import shared.Values.minScreenshotScale
import shared.Values.scrollZoomFactor
import viewmodel.extraUiLogic.extractDisplayGraphics
import viewmodel.extraUiLogic.forFirstNodeUnder
import viewmodel.extraUiLogic.getFlatXmlTreeMap
import viewmodel.extraUiLogic.getNodesOrderedByDepth
import viewmodel.extraUiLogic.propertyMap
import java.io.FileInputStream
import kotlin.coroutines.CoroutineContext
import kotlin.math.min

class AppViewModel(
    private val coroutineManager: CoroutineManager
) {
    /* Model */

    private val layoutInspector = LayoutInspector(
        coroutineManager = CoroutineManager(
            customCoroutineScope = CoroutineScope(Dispatchers.IO + Job())
        ),
        extractionManager = getExtractionManager()
    )

    /* Screenshot Layer */

    var screenshotLayerOffset by mutableStateOf(Offset.Zero)
    var screenshotLayerScale by mutableStateOf(1f)

    val showScreenshot get() = layoutInspector.state == InspectorState.POPULATED
    val imageBitmap by derivedStateOf {
        loadImageBitmap(FileInputStream(layoutInspector.data.screenshotFile)).apply {
            // Store the screenshot file size as soon as possible.
            screenshotFileSize = Size(height = height.toFloat(), width = width.toFloat())
        }
    }
    // size of the image composable
    private var screenshotComposableSize by mutableStateOf(Size.Unspecified)

    val showHighlighter get() = showScreenshot && layoutInspector.isNodeSelected
    val highlighterOffset by derivedStateOf { selectedNodeDisplayGraphics.offset }
    val highlighterSize by derivedStateOf { selectedNodeDisplayGraphics.size }
    private val selectedNodeDisplayGraphics
        get() = layoutInspector.selectedNode.extractDisplayGraphics(displayPixelConversionFactor)

    val highlighterStrokeWidth get() = (defaultHighlighterStrokeWidth / screenshotLayerScale).toPx(density)

    // This does not to be a state as it does not have to trigger any recomposition. It is initialized when the model
    // updates the data and thus the imageBitmap.
    private var screenshotFileSize = Size.Unspecified

    // It is irrelevant whether we use width or height when calculating the conversion factor.
    private val displayPixelConversionFactor
        get() = screenshotComposableSize.height / screenshotFileSize.height

    @Suppress("UNUSED_PARAMETER")
    fun onImageGesture(centroid: Offset, pan: Offset, zoom: Float, rotation: Float) {
        screenshotLayerOffset += pan * screenshotLayerScale
    }

    fun onImageSizeChanged(size: IntSize) {
        screenshotComposableSize = size.toSize()
    }

    fun onImageTap(offset: Offset, uiCoroutineContext: CoroutineContext) {
        // Extract node list with the first nodes being the deepest ones.
        val flatNodeListByDepth = layoutInspector.data.root.getNodesOrderedByDepth(deepNodesFirst = true)

        flatNodeListByDepth.forFirstNodeUnder(
            offset = offset,
            displayPixelConversionFactor = displayPixelConversionFactor
        ) { matchingNode ->
            // Update the selected node if a matching node was found.
            layoutInspector.selectNode(node = matchingNode)

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

    private fun scrollToSelectedNode(uiCoroutineContext: CoroutineContext) {
        coroutineManager.launch {
            withContext(uiCoroutineContext) {
                // Scroll to the selected node in the upper right box.
                upperPaneLazyListState.animateScrollToItem(
                    index = flatXmlTreeMap.keys.indexOf(layoutInspector.selectedNode),
                    // Divide the upper pane height by 2 so that the selected node ends up in the center of the Box.
                    scrollOffset = - upperPaneHeight.toPx(density).div(2).toInt()
                )
            }
        }
    }

    /* Button Layer */

    val isExtractButtonEnabled get() = layoutInspector.state != InspectorState.WAITING
    val extractButtonText
        get() = when (layoutInspector.state) {
            InspectorState.WAITING -> "Dumping..."
            else -> "Smash red button to dump."
        }

    val areResizeButtonsEnabled get() = layoutInspector.state == InspectorState.POPULATED

    fun onExtractButtonPressed() = layoutInspector.extractLayout()

    // This is a method that is highly dependent on the specific arrangement of the UI components on the screen. It will
    // likely break when the UI undergoes significant change.
    fun onFitScreenshotToScreenButtonPressed() {
        // We first place the screenshot in the center of the screenshot area (the area between the buttons and the
        // panes).

        // The reference of this offset is the upper-left corner (x = 0, y = 0).
        val screenshotComposableGlobalOffsetAtCenter = Offset(
            x = largePadding.toPx(density) + screenshotComposableSize.width / 2,
            y = panesHeightConstraint.toPx(density) / 2
        )

        val topVisibleScreenshotAreaBound = mediumPadding + extractButtonDiameter
        val bottomVisibleScreenshotAreaBound = panesHeightConstraint
        val startVisibleScreenshotAreaBound = mediumPadding + extractButtonDiameter
        val endVisibleScreenshotAreaBound =
            panesWidthConstraint - (wedgeSmallDimension + smallPadding + paneWidth + mediumPadding)

        // The reference of this offset is the upper-left corner (x = 0, y = 0) as well.
        val targetGlobalOffsetAtCenter = Offset(
            x = (startVisibleScreenshotAreaBound + endVisibleScreenshotAreaBound).toPx(density) / 2,
            y = (topVisibleScreenshotAreaBound + bottomVisibleScreenshotAreaBound).toPx(density) / 2
        )

        screenshotLayerOffset = targetGlobalOffsetAtCenter - screenshotComposableGlobalOffsetAtCenter

        // Then we resize the screenshot to take up as much space as possible (the actual "fit-to-screen").

        val targetHorizontalScale =
            (endVisibleScreenshotAreaBound - startVisibleScreenshotAreaBound)
                .toPx(density)
                .div(screenshotComposableSize.width)
        val targetVerticalScale =
            (bottomVisibleScreenshotAreaBound - topVisibleScreenshotAreaBound)
                .toPx(density)
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

    /* Panes Layer */

    var paneWidth by mutableStateOf(initialPaneWidth)

    var upperPaneHeight by mutableStateOf(initialUpperPaneHeight)
    val upperPaneLazyListState = LazyListState()
    val upperPaneHorizontalScrollState = ScrollState(initial = 0)

    // The lower Pane takes up as much height as possible. Value gets updated with the actual height at composition.
    private var lowerPaneHeight by mutableStateOf(Dp.Unspecified)
    val lowerPaneVerticalScrollState = ScrollState(initial = 0)
    val lowerPaneHorizontalScrollState = ScrollState(initial = 0)

    // panesHeightConstraint and panesWidthConstraint are, under the current implementation, also the height and width
    // of the window content area (i.e. the total area, excluding the window bar). These values do not need to be states
    // as they are only used as limits in the wedge drag event handlers.
    private var panesHeightConstraint = Dp.Unspecified
    private var panesWidthConstraint = Dp.Unspecified

    val showXmlTree get() = layoutInspector.state == InspectorState.POPULATED
    private val flatXmlTreeMap
        get() = layoutInspector.data.root.getFlatXmlTreeMap(
            selectedNode = layoutInspector.selectedNode,
            onNodeTreeLineClicked = ::onNodeTreeLineClicked
        )
    val flatXmlTree get() = flatXmlTreeMap.values.toList()

    val showSelectedNodeProperties get() = showXmlTree && layoutInspector.isNodeSelected
    val selectedNodePropertyMap get() = layoutInspector.selectedNode.propertyMap

    fun onVerticalWedgeDrag(
        change: PointerInputChange,
        dragAmount: Offset
    ) {
        if (change.positionChange() != Offset.Zero) change.consume()

        val dragAmountDp = dragAmount.x.toDp(density)
        paneWidth = (paneWidth - dragAmountDp).coerceIn(
            minimumValue = minimumPaneDimension,
            maximumValue = panesWidthConstraint - minimumPaneDimension
        )
    }

    fun onHorizontalWedgeDrag(
        change: PointerInputChange,
        dragAmount: Offset
    ) {
        if (change.positionChange() != Offset.Zero) change.consume()

        val dragAmountDp = dragAmount.y.toDp(density)
        upperPaneHeight = (upperPaneHeight + dragAmountDp).coerceIn(
            minimumValue = minimumPaneDimension,
            maximumValue = panesHeightConstraint - minimumPaneDimension
        )
    }

    fun onLowerPaneSizeChanged(size: IntSize) {
        lowerPaneHeight = size.height.toDp(density)
    }

    private fun onNodeTreeLineClicked(node: Node) = layoutInspector.selectNode(node)

    /* Misc */

    private var density by mutableStateOf(Float.NaN)

    fun onNewDensity(density: Float) {
        this.density = density
    }

    @OptIn(ExperimentalComposeUiApi::class)
    fun onWindowKeyEvent(event: KeyEvent) =
        when {
            event.type == KeyEventType.KeyDown -> {
                when (event.key) {
                    Key.D -> {
                        if (isExtractButtonEnabled) {
                            onExtractButtonPressed()
                        }
                        true
                    }

                    Key.DirectionUp -> {
                        if (areResizeButtonsEnabled) {
                            onEnlargeScreenshotButtonPressed()
                        }
                        true
                    }

                    Key.DirectionDown -> {
                        if (areResizeButtonsEnabled) {
                            onShrinkScreenshotButtonPressed()
                        }
                        true
                    }

                    Key.M -> {
                        if (areResizeButtonsEnabled) {
                            onFitScreenshotToScreenButtonPressed()
                        }
                        true
                    }

                    else -> false
                }
            }

            else -> false
        }

    fun onPanesHeightConstraintChanged(newHeightConstraint: Dp) {
        // First update the height constraint.
        panesHeightConstraint = newHeightConstraint

        // Then make sure that the upper pane height is within limits. This is relevant when the window gets resized
        // and the height constraint gets smaller, but the upper pane height stays the same.
        upperPaneHeight = upperPaneHeight.coerceIn(
            minimumValue = minimumPaneDimension,
            maximumValue = panesHeightConstraint - minimumPaneDimension
        )
    }

    fun onPanesWidthConstraintChanged(newWidthConstraint: Dp) {
        // First update the width constraint.
        panesWidthConstraint = newWidthConstraint

        // Then make sure that the pane width is within limits. This is relevant when the window gets resized and the
        // width constraint gets smaller, but the pane width stays the same.
        paneWidth = paneWidth.coerceIn(
            minimumValue = minimumPaneDimension,
            maximumValue = panesWidthConstraint - minimumPaneDimension
        )
    }

    fun teardown() {
        coroutineManager.teardown()
        layoutInspector.teardown()
    }
}