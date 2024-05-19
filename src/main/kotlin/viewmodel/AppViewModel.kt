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
import model.parser.xmlElements.Node
import viewmodel.Dimensions.Initial.initialPaneWidth
import viewmodel.Dimensions.Initial.initialUpperPaneHeight
import viewmodel.Dimensions.minimumPaneDimension
import viewmodel.extraUiLogic.extractDisplayGraphics
import viewmodel.extraUiLogic.forFirstNodeUnder
import viewmodel.extraUiLogic.getFlatXmlTree
import viewmodel.extraUiLogic.getNodesOrderedByDepth
import viewmodel.extraUiLogic.propertyMap
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
        Graphics(offset = Offset.Zero, size = Size.Unspecified)
    ) // Offset is Zero and not Unspecified because it is needed for composing the Image.

    val showHighlighter get() = layoutInspector.isNodeSelected
    val highlighterGraphics: Graphics
        get() {
            val selectedNodeGraphics =
                layoutInspector.selectedNode.extractDisplayGraphics(displayPixelConversionFactor)

            return Graphics(
                offset = imageComposableGraphics.offset + selectedNodeGraphics.offset,
                size = selectedNodeGraphics.size
            )
        }

    val showScreenshot get() = layoutInspector.state == InspectorState.POPULATED
    val imageBitmap
        get() = loadImageBitmap(
            FileInputStream(File(layoutInspector.data.screenshotPath)) // TODO refactor
        ).apply {
            // Store the screenshot file size as soon as possible.
            screenshotFileSize = Size(height = height.toFloat(), width = width.toFloat())
        }

    // This does not to be a state as it does not have to trigger any recomposition. It is initialized when the model
    // updates the data and thus the imageBitmap.
    private var screenshotFileSize = Size.Unspecified

    // It is irrelevant whether we use width or height when calculating the conversion factor.
    private val displayPixelConversionFactor
        get() = imageComposableGraphics.size.height / screenshotFileSize.height

    fun onImageGesture(centroid: Offset, pan: Offset, zoom: Float, rotation: Float) {
        val oldOffset = imageComposableGraphics.offset
        imageComposableGraphics = imageComposableGraphics.copy(offset = oldOffset + pan)
    }

    fun onImageSizeChanged(size: IntSize) {
        imageComposableGraphics = imageComposableGraphics.copy(size = size.toSize())
    }

    fun onImageTap(offset: Offset, uiCoroutineContext: CoroutineContext) {
        // Extract node list with the first nodes being the deepest ones.
        val flatNodeList = layoutInspector.data.root
            .getNodesOrderedByDepth(deepNodesFirst = true)
            // TODO this is called on every tap. Store the list.

        flatNodeList.forFirstNodeUnder(
            offset = offset,
            displayPixelConversionFactor = displayPixelConversionFactor
        ) { matchingNode ->
            // Update the selected node if a matching node was found.
            layoutInspector.selectNode(node = matchingNode)

            coroutineManager.launch {
                withContext(uiCoroutineContext) {
                    // Scroll to the selected node in the upper right box.
                    upperPaneVerticalScrollState.animateScrollTo(
                        value = upperPaneNodePositions[layoutInspector.selectedNode]!! - 300
                            // TODO remove magic number
                    )
                }
            }
        }
    }

    /* Button Layer */

    val showButtonText get() = layoutInspector.state != InspectorState.POPULATED

    fun onExtractButtonPressed() = layoutInspector.extractLayout()

    /* Panes Layer */

    var paneWidth by mutableStateOf(initialPaneWidth)

    var upperPaneHeight by mutableStateOf(initialUpperPaneHeight)
    val upperPaneVerticalScrollState = ScrollState(initial = 0)
    val upperPaneHorizontalScrollState = ScrollState(initial = 0)
    private val upperPaneNodePositions = mutableMapOf<Node, Int>()

    // The lower Pane takes up as much height as possible. Value gets updated with the actual height at composition.
    private var lowerPaneHeight by mutableStateOf(Dp.Unspecified)
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

    fun onLowerPaneSizeChanged(size: IntSize, density: Float) {
        lowerPaneHeight = (size.height / density).dp
    }

    private fun onNodeTreeLineClicked(node: Node) = layoutInspector.selectNode(node)

    private fun onNodeTreeLineGloballyPositioned(layoutCoordinates: LayoutCoordinates, node: Node) {
        // Capture the position of each node row.
        upperPaneNodePositions[node] = layoutCoordinates.positionInParent().y.roundToInt()
    }

    /* Misc */

    fun teardown() {
        coroutineManager.teardown()
        layoutInspector.teardown()
    }
}