package viewmodel

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.coerceIn
import kotlinx.coroutines.withContext
import oldModel.CoroutineManager
import oldModel.InspectorState
import oldModel.parser.xmlElements.Node
import oldModel.parser.xmlElements.System
import shared.Dimensions.Initial.initialPaneWidth
import shared.Dimensions.Initial.initialUpperPaneHeight
import shared.Dimensions.minimumPaneDimension
import viewmodel.extraUiLogic.getFlatXmlTreeMap
import viewmodel.extraUiLogic.propertyMap
import kotlin.coroutines.CoroutineContext

class PaneState(
    private val getInspectorState: () -> InspectorState,
    private val getDataRoot: () -> System,
    private val isNodeSelected: () -> Boolean,
    private val getSelectedNode: () -> Node,
    private val selectNode: (Node) -> Unit,
    private val getDensity: () -> Float,
    private val getPanesConstraint: () -> DpSize,
    private val coroutineManager: CoroutineManager
) {
    var paneWidth by mutableStateOf(initialPaneWidth)

    var upperPaneHeight by mutableStateOf(initialUpperPaneHeight)
    val upperPaneLazyListState = LazyListState()
    val upperPaneHorizontalScrollState = ScrollState(initial = 0)

    // The lower Pane takes up as much height as possible. Value gets updated with the actual height at composition.
    private var lowerPaneHeight by mutableStateOf(Dp.Unspecified)
    val lowerPaneVerticalScrollState = ScrollState(initial = 0)
    val lowerPaneHorizontalScrollState = ScrollState(initial = 0)

    val showXmlTree get() = getInspectorState() == InspectorState.POPULATED
    private val flatXmlTreeMap
        get() = getDataRoot().getFlatXmlTreeMap(
            selectedNode = getSelectedNode(),
            onNodeTreeLineClicked = ::onNodeTreeLineClicked
        )
    val flatXmlTree get() = flatXmlTreeMap.values.toList()

    val showSelectedNodeProperties get() = showXmlTree && isNodeSelected()
    val selectedNodePropertyMap get() = getSelectedNode().propertyMap

    fun onVerticalWedgeDrag(
        change: PointerInputChange,
        dragAmount: Offset
    ) {
        if (change.positionChange() != Offset.Zero) change.consume()

        val dragAmountDp = dragAmount.x.toDp(getDensity())
        paneWidth = (paneWidth - dragAmountDp).coerceIn(
            minimumValue = minimumPaneDimension,
            maximumValue = getPanesConstraint().width - minimumPaneDimension
        )
    }

    fun onHorizontalWedgeDrag(
        change: PointerInputChange,
        dragAmount: Offset
    ) {
        if (change.positionChange() != Offset.Zero) change.consume()

        val dragAmountDp = dragAmount.y.toDp(getDensity())
        upperPaneHeight = (upperPaneHeight + dragAmountDp).coerceIn(
            minimumValue = minimumPaneDimension,
            maximumValue = getPanesConstraint().height - minimumPaneDimension
        )
    }

    fun onLowerPaneSizeChanged(size: IntSize) {
        lowerPaneHeight = size.height.toDp(getDensity())
    }

    fun scrollToSelectedNode(uiCoroutineContext: CoroutineContext) {
        coroutineManager.launch {
            withContext(uiCoroutineContext) {
                // Scroll to the selected node in the upper right box.
                upperPaneLazyListState.animateScrollToItem(
                    index = flatXmlTreeMap.keys.indexOf(getSelectedNode()),
                    // Divide the upper pane height by 2 so that the selected node ends up in the center of the Box.
                    scrollOffset = - upperPaneHeight.toPx(getDensity()).div(2).toInt()
                )
            }
        }
    }

    fun coercePaneHeight() {
        upperPaneHeight = upperPaneHeight.coerceIn(
            minimumValue = minimumPaneDimension,
            maximumValue = getPanesConstraint().height - minimumPaneDimension
        )
    }

    fun coercePaneWidth() {
        paneWidth = paneWidth.coerceIn(
            minimumValue = minimumPaneDimension,
            maximumValue = getPanesConstraint().width - minimumPaneDimension
        )
    }

    private fun onNodeTreeLineClicked(node: Node) = selectNode(node)
}