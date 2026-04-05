package view.panes

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import model.InspectorState
import model.displayDataResolver.DisplayData
import model.parser.dataClasses.GenericNode
import view.utils.getFlatXmlTreeMap
import view.utils.propertyMap

class PaneState(
    inspectorState: StateFlow<InspectorState>,
    displayData: StateFlow<DisplayData>,
    isNodeSelected: StateFlow<Boolean>,
    val selectedNode: StateFlow<GenericNode>,
    selectNode: (GenericNode) -> Unit,
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    val showXmlTree = inspectorState.map { it == InspectorState.POPULATED }
    private val flatXmlTreeMap =
        displayData
            .map { it.displayNode.getFlatXmlTreeMap { node: GenericNode -> selectNode(node) } }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyMap(),
            )

    val flatXmlTree = flatXmlTreeMap.map { it.values.toList() }

    val selectedNodeIndex =
        combine(flatXmlTreeMap, selectedNode) { flatXmlTreeMap, selectedNode ->
            flatXmlTreeMap.keys.indexOf(selectedNode)
        }
    val activateScroll =
        combine(inspectorState, isNodeSelected) { inspectorState, isNodeSelected ->
            inspectorState == InspectorState.POPULATED && isNodeSelected
        }

    val showSelectedNodeProperties =
        combine(showXmlTree, isNodeSelected) { showXmlTree, isNodeSelected ->
            showXmlTree && isNodeSelected
        }
    val selectedNodePropertyMap = selectedNode.map { it.propertyMap }

    init {
        collectSelectedNodes()
    }

    private fun collectSelectedNodes() {
        coroutineScope.launch {
            var lastSelectedNode: GenericNode? = null

            selectedNode
                .transform {
                    val newSelectedNodes = SelectedNodes(current = it, last = lastSelectedNode)
                    lastSelectedNode = it

                    emit(newSelectedNodes)
                }
                .collect { propagateNodeSelection(it) }
        }
    }

    private fun propagateNodeSelection(selectedNodes: SelectedNodes) {
        val treeLineToSelect = flatXmlTreeMap.value[selectedNodes.current]

        val lastSelectedNode = selectedNodes.last
        val treeLineToDeselect =
            if (lastSelectedNode != null) {
                flatXmlTreeMap.value.get(selectedNodes.last)
            } else {
                null
            }

        treeLineToSelect?.apply {
            expandUntilVisible()
            select()
        }
        treeLineToDeselect?.deselect()
    }

    // Todo: use
    fun collapseAllLines() {
        val allLines = flatXmlTreeMap.value.values
        allLines.forEach { it.collapse() }
    }

    // Todo: use
    fun expandAllLines() {
        val allLines = flatXmlTreeMap.value.values
        allLines.forEach { it.expand() }
    }

    // Todo: use
    fun expandToSelectedNode() {
        val selectedLine = flatXmlTreeMap.value[selectedNode.value]
        selectedLine?.expandUntilVisible()
    }
}

private data class SelectedNodes(val current: GenericNode, val last: GenericNode?)
