package viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import oldModel.InspectorState
import oldModel.parser.xmlElements.Node
import oldModel.parser.xmlElements.System
import viewmodel.extraUiLogic.extractDisplayGraphics
import viewmodel.extraUiLogic.forFirstNodeUnder
import viewmodel.extraUiLogic.getNodesOrderedByDepth
import java.io.File
import java.io.FileInputStream
import kotlin.coroutines.CoroutineContext

class ScreenshotState(
    private val getInspectorState: () -> InspectorState,
    private val getScreenshotFile: () -> File,
    private val getDataRoot: () -> System,
    private val isNodeSelected: () -> Boolean,
    private val getSelectedNode: () -> Node,
    private val selectNode: (Node) -> Unit
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

    // size of the image composable
    var screenshotComposableSize by mutableStateOf(Size.Unspecified)
        private set

    // It is irrelevant whether we use width or height when calculating the conversion factor.
    private val displayPixelConversionFactor
        get() = screenshotComposableSize.height / screenshotFileSize.height

    val showHighlighter get() = showScreenshot && isNodeSelected()
    val selectedNodeDisplayGraphics get() = getSelectedNode().extractDisplayGraphics(displayPixelConversionFactor)

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
        }
    }
}
