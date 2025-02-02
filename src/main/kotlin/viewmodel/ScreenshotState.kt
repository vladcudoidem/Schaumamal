package viewmodel

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
import model.DisplayData
import model.InspectorState
import model.parser.xmlElements.GenericNode
import viewmodel.extraUiLogic.extractDisplayGraphics
import viewmodel.extraUiLogic.forFirstNodeUnder
import viewmodel.extraUiLogic.getNodesOrderedByDepth
import java.io.FileInputStream
import kotlin.coroutines.CoroutineContext

class ScreenshotState(
    inspectorState: StateFlow<InspectorState>,
    isNodeSelected: StateFlow<Boolean>,
    selectedNode: StateFlow<GenericNode>,
    private val data: StateFlow<DisplayData>,
    private val selectNode: (GenericNode) -> Unit
) {
    val showScreenshot = inspectorState.map { it == InspectorState.POPULATED }
    val imageBitmap = data.map {
        withContext(Dispatchers.IO) { // Todo: is "withContext(Dispatchers.IO)" a fitting solution for blocking call?
            val defaultBitmap = ImageBitmap(0, 0)
            val actualBitmap = if (it != DisplayData.Empty) {
                loadImageBitmap(FileInputStream(it.screenshotFile))
            } else {
                defaultBitmap
            }

            actualBitmap.apply {
                // Store the screenshot file size as soon as possible.
                screenshotFileSize = Size(height = height.toFloat(), width = width.toFloat())
            }
        }
    } // Todo: this was using "derivedStateOf". Is the current implementation good?

    // This does not to be a state as it does not have to trigger any recomposition. It is initialized when the model
    // updates the data and thus the imageBitmap.
    private var screenshotFileSize = Size.Irrelevant

    // size of the image composable
    private val _screenshotComposableSize = MutableStateFlow(Size.Irrelevant)
    val screenshotComposableSize get() = _screenshotComposableSize.asStateFlow()

    // It is irrelevant whether we use width or height when calculating the conversion factor.
    private val displayPixelConversionFactor: StateFlow<Float> =
        _screenshotComposableSize
            .map {
                it.height / screenshotFileSize.height
            }
            .stateIn(
                scope = CoroutineScope(Dispatchers.Default),
                started = SharingStarted.Eagerly,
                initialValue = 1.0f // Todo: is this a fitting initial value?
            )

    val showHighlighter = combine(showScreenshot, isNodeSelected) { showScreenshot, isNodeSelected ->
        showScreenshot && isNodeSelected
    }
    val selectedNodeDisplayGraphics =
        combine(selectedNode, displayPixelConversionFactor) { selectedNode, displayPixelConversionFactor ->
            selectedNode.extractDisplayGraphics(displayPixelConversionFactor)
        }

    fun onImageSizeChanged(size: IntSize) {
        _screenshotComposableSize.value = size.toSize()
    }

    fun onImageTap(offset: Offset, uiCoroutineContext: CoroutineContext) {
        // Extract node list with the first nodes being the deepest ones.
        val flatNodeListByDepth = data.value.displayNode.getNodesOrderedByDepth(deepNodesFirst = true)

        flatNodeListByDepth.forFirstNodeUnder(
            offset = offset,
            displayPixelConversionFactor = displayPixelConversionFactor.value
        ) { matchingNode ->
            // Update the selected node if a matching node was found.
            selectNode(matchingNode)
        }
    }
}
