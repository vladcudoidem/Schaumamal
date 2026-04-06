package view.panes

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import model.InspectorState
import model.displayDataResolver.DisplayData
import model.parser.dataClasses.GenericNode
import model.parser.dataClasses.WindowNode
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

    private val selectedNodeIndex =
        combine(flatXmlTreeMap, selectedNode) { flatXmlTreeMap, selectedNode ->
                flatXmlTreeMap.keys.indexOf(selectedNode)
            }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.Eagerly,
                // Todo: this default is ok currently because we only scroll when we are allowed
                initialValue = 0,
            )
    // Todo: remove this and use nullable states or something...
    private val isTreeScrollAllowed =
        combine(inspectorState, isNodeSelected) { inspectorState, isNodeSelected ->
            inspectorState == InspectorState.POPULATED && isNodeSelected
        }

    private val automaticTreeScrollEvents =
        selectedNodeIndex.map { TreeScrollEvent(targetIndex = it) }
    private val manualTreeScrollEvents = MutableSharedFlow<TreeScrollEvent>(extraBufferCapacity = 1)

    val combinedTreeScrollEvents =
        merge(automaticTreeScrollEvents, manualTreeScrollEvents).combineTransform(
            isTreeScrollAllowed
        ) { scrollEvent, isScrollAllowed ->
            if (isScrollAllowed) {
                emit(scrollEvent)
            }
        }

    val showSelectedNodeProperties =
        combine(showXmlTree, isNodeSelected) { showXmlTree, isNodeSelected ->
            showXmlTree && isNodeSelected
        }
    val selectedNodePropertyMap = selectedNode.map { it.propertyMap }

    val isExpandAndScrollButtonEnabled = isNodeSelected

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

    // Select and expands to the new selected node and deselect the old one.
    private fun propagateNodeSelection(selectedNodes: SelectedNodes) {
        val treeLineToSelect = flatXmlTreeMap.value[selectedNodes.current]

        val lastSelectedNode = selectedNodes.last
        val treeLineToDeselect =
            if (lastSelectedNode != null) {
                flatXmlTreeMap.value[selectedNodes.last]
            } else {
                null
            }

        treeLineToSelect?.apply {
            expandUntilVisible()
            select()
        }
        treeLineToDeselect?.deselect()
    }

    fun collapseAllLines(excludeDisplayNodes: Boolean = true) {
        val lines =
            flatXmlTreeMap.value
                .filter {
                    if (excludeDisplayNodes) {
                        it.key is GenericNode || it.key is WindowNode
                    } else {
                        true
                    }
                }
                .values

        lines.forEach { it.collapse() }
    }

    fun expandAllLines() {
        val allLines = flatXmlTreeMap.value.values
        allLines.forEach { it.expand() }
    }

    fun expandAndScrollToSelectedNode() {
        val selectedLine = flatXmlTreeMap.value[selectedNode.value]
        selectedLine?.expandUntilVisible()

        val targetIndex = selectedNodeIndex.value
        manualTreeScrollEvents.tryEmit(TreeScrollEvent(targetIndex))
    }
}

private data class SelectedNodes(val current: GenericNode, val last: GenericNode?)

data class TreeScrollEvent(val targetIndex: Int)
