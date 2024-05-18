package viewmodel

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import model.InspectorState
import model.LayoutInspector
import model.utils.CoroutineManager
import shared.graphics.HighlighterGraphics
import shared.graphics.ImageGraphics
import shared.graphics.RawNodeGraphics
import shared.xmlElements.Node
import view.Dimensions.Initial.initialPaneWidth
import view.Dimensions.Initial.initialUpperPaneHeight
import view.Dimensions.minimumPaneDimension
import java.io.File
import java.io.FileInputStream
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt

class AppViewModel(
    private val coroutineManager: CoroutineManager
) {
    /* Model */

    private val layoutInspector = LayoutInspector(
        coroutineManager = CoroutineManager(
            customCoroutineScope = CoroutineScope(Dispatchers.IO + Job())
        )
    )

    /* Screenshot Layer */

    var imageComposableGraphics by mutableStateOf(
        ImageGraphics(offset = Offset.Zero, size = Size.Unspecified)
    ) // Offset is Zero and not Unspecified because it is needed for composing the Image
        // TODO does this need to be a state?

    private var screenshotFileSizePx = Size.Unspecified
        // This does not to be a state as it does not have to trigger any recomposition.

    val showHighlighter get() = layoutInspector.isNodeSelected
    val highlighterGraphics
        get() = HighlighterGraphics.from(
            imageOffset = imageComposableGraphics.offset,
            imageSize = imageComposableGraphics.size,
            screenshotFileSize = screenshotFileSizePx,
            selectedNodeGraphics = RawNodeGraphics.from(layoutInspector.selectedNode)
        )

    val showImage get() = layoutInspector.state == InspectorState.POPULATED
    val imageBitmap
        get() = loadImageBitmap(
            FileInputStream(File(layoutInspector.data.screenshotPath))
        ).apply {
            // Store the screenshot file size as soon as possible.
            screenshotFileSizePx = Size(height = height.toFloat(), width = width.toFloat())
        }

    fun onImageGesture(centroid: Offset, pan: Offset, zoom: Float, rotation: Float) {
        val oldOffset = imageComposableGraphics.offset
        imageComposableGraphics = imageComposableGraphics.copy(offset = oldOffset + pan)
    }

    fun onImageSizeChanged(size: IntSize) {
        imageComposableGraphics = imageComposableGraphics.copy(size = size.toSize())
    }

    fun onImageTap(offset: Offset, uiCoroutineContext: CoroutineContext) {
        val scalingFactor = screenshotFileSizePx.height / imageComposableGraphics.size.height
            // It is irrelevant whether we use width or height when calculating the scaling factor.
            // TODO create a view model property for this factor and use it in HighlighterGraphics too
        val scaledOffset = offset * scalingFactor // TODO coerce

        val flatNodeList = layoutInspector.data.root.getNodesFlattened()
            // This is called on every Tap. TODO store flattened nodes.
        flatNodeList.forNodeUnder(offset = scaledOffset) {
            layoutInspector.selectNode(node = it)

            coroutineManager.launch {
                withContext(uiCoroutineContext) {

                    // Scroll to the selected node in the upper right box.
                    upperPaneVerticalScrollState.animateScrollTo(
                        value = upperPaneNodePositions[layoutInspector.selectedNode] ?: 0
                    )
                }
            }
        }
    }

    /* Button Layer */

    val showButtonText get() = layoutInspector.state != InspectorState.POPULATED
    val buttonText
        get() = when (layoutInspector.state) {
            InspectorState.POPULATED -> ""
            InspectorState.EMPTY -> "...smash the red button"
            InspectorState.WAITING -> "...dumping"
        } // TODO store the string literals somewhere else maybe?

    fun onExtractButtonPressed() = layoutInspector.extractLayout()

    /* Panes Layer */

    var paneWidth by mutableStateOf(initialPaneWidth)
        // TODO should Dimensions be in shared?

    var upperPaneHeight by mutableStateOf(initialUpperPaneHeight)
    val upperPaneVerticalScrollState = ScrollState(initial = 0)
    val upperPaneHorizontalScrollState = ScrollState(initial = 0)
    val upperPaneNodePositions = mutableMapOf<Node, Int>()

    private var lowerPaneHeight by mutableStateOf(Dp.Unspecified)
        // The lower Pane takes up as much height as possible. Value gets updated with the actual height at composition.
    val lowerPaneVerticalScrollState = ScrollState(initial = 0)
    val lowerPaneHorizontalScrollState = ScrollState(initial = 0)

    val showXmlTree get() = layoutInspector.state == InspectorState.POPULATED
    val flatXmlTree
        get() = layoutInspector.data.root.getFlatXmlTree(
            selectedNode = layoutInspector.selectedNode,
            onNodeTreeLineClicked = ::onNodeTreeLineClicked,
            onNodeTreeLineGloballyPositioned = ::onNodeTreeLineGloballyPositioned
        )

    val showSelectedNodeProperties get() = layoutInspector.isNodeSelected
    val selectedNodePropertyMap get() = layoutInspector.selectedNode.propertyMap

    fun onVerticalWedgeDrag(
        change: PointerInputChange,
        dragAmount: Offset,
        density: Float
    ) {
        if (change.positionChange() != Offset.Zero) change.consume()

        val dragAmountDp = (dragAmount.x / density).dp
        paneWidth = (paneWidth - dragAmountDp).coerceAtLeast(
            minimumPaneDimension
        )
    }

    fun onHorizontalWedgeDrag(
        change: PointerInputChange,
        dragAmount: Offset,
        density: Float
    ) {
        if (change.positionChange() != Offset.Zero) change.consume()

        val dragAmountDp = (dragAmount.y / density).dp
        if (lowerPaneHeight >= minimumPaneDimension || dragAmountDp < 0.dp) {
            upperPaneHeight = (upperPaneHeight + dragAmountDp).coerceAtLeast(
                minimumPaneDimension
            )
        }
    }

    fun onNodeTreeLineClicked(node: Node) = layoutInspector.selectNode(node)

    fun onNodeTreeLineGloballyPositioned(layoutCoordinates: LayoutCoordinates, node: Node) {
        // Capture the position of each node row.
        upperPaneNodePositions[node] = layoutCoordinates.positionInParent().y.roundToInt() - 300
            // TODO remove the magic number 300
    }

    fun onLowerPaneSizeChanged(size: IntSize, density: Float) {
        lowerPaneHeight = (size.height / density).dp
    }

    /* Misc */

    fun teardown() {
        coroutineManager.teardown()
        layoutInspector.teardown()
    }
}