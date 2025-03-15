package view.screenshot

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import model.InspectorState
import model.displayDataResolver.DisplayData
import model.parser.dataClasses.GenericNode
import view.utils.Irrelevant
import view.utils.area
import view.utils.getGraphics
import view.utils.getNodesUnder
import java.io.FileInputStream
import kotlin.coroutines.CoroutineContext

class ScreenshotState(
    inspectorState: StateFlow<InspectorState>,
    isNodeSelected: StateFlow<Boolean>,
    selectedNode: StateFlow<GenericNode>,
    private val displayData: StateFlow<DisplayData>,
    private val selectNode: (GenericNode) -> Unit
) {
    val showScreenshot = inspectorState.map { it == InspectorState.POPULATED }
    val imageBitmap = displayData.map {
        withContext(Dispatchers.IO) { // Todo: is "withContext(Dispatchers.IO)" a fitting solution for blocking call?
            val defaultBitmap = ImageBitmap(0, 0)
            val actualBitmap = if (it == DisplayData.Empty) {
                defaultBitmap
            } else {
                // Todo: refactor deprecated code.
                // Todo: use FileImageInputStream?
                loadImageBitmap(FileInputStream(it.screenshotFile))
            }

            actualBitmap.apply {
                // Store the screenshot file size as soon as possible.
                screenshotFileSize.value = Size(height = height.toFloat(), width = width.toFloat())
            }
        }
    } // Todo: this was using "derivedStateOf". Is the current implementation good?

    private val screenshotFileSize = MutableStateFlow(Size.Irrelevant)

    // size of the image composable
    private val _screenshotComposableSize = MutableStateFlow(Size.Irrelevant)
    val screenshotComposableSize get() = _screenshotComposableSize.asStateFlow()

    // It is irrelevant whether we use width or height when calculating the conversion factor.
    private val displayPixelConversionFactor: StateFlow<Float> =
        combine(_screenshotComposableSize, screenshotFileSize) { _screenshotComposableSize, screenshotFileSize ->
            _screenshotComposableSize.height / screenshotFileSize.height
        }.stateIn(
            scope = CoroutineScope(Dispatchers.Default),
            started = SharingStarted.Eagerly,
            initialValue = 1.0f // Todo: is this a fitting initial value?
        )

    val showHighlighter = combine(showScreenshot, isNodeSelected) { showScreenshot, isNodeSelected ->
        showScreenshot && isNodeSelected
    }
    val selectedNodeDisplayGraphics =
        combine(selectedNode, displayPixelConversionFactor) { selectedNode, displayPixelConversionFactor ->
            selectedNode.getGraphics(displayPixelConversionFactor)
        }

    fun onImageSizeChanged(size: IntSize) {
        _screenshotComposableSize.value = size.toSize()
    }

    fun onImageTap(offset: Offset, uiCoroutineContext: CoroutineContext) {
        // Extract nodes that are under the tap location.
        val nodes = displayData.value.displayNode.getNodesUnder(offset, displayPixelConversionFactor.value)
        val nodeAreas = nodes.map { it.getGraphics().size.area }

        val smallestNode = nodes[nodeAreas.indexOf(nodeAreas.min())]
        selectNode(smallestNode)
    }
}
