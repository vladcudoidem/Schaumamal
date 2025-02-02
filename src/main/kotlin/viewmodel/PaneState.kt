package viewmodel

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import model.DisplayData
import model.InspectorState
import model.parser.xmlElements.GenericNode
import viewmodel.extraUiLogic.getFlatXmlTreeMap
import viewmodel.extraUiLogic.propertyMap

class PaneState(
    inspectorState: StateFlow<InspectorState>,
    data: StateFlow<DisplayData>,
    isNodeSelected: StateFlow<Boolean>,
    selectedNode: StateFlow<GenericNode>,
    selectNode: (GenericNode) -> Unit
) {

    val showXmlTree = inspectorState.map { it == InspectorState.POPULATED }
    private val flatXmlTreeMap = combine(data, selectedNode) { dataRoot, selectedNode ->
        dataRoot
            .displayNode
            .getFlatXmlTreeMap(
                selectedNode = selectedNode,
                onNodeTreeLineClicked = { node: GenericNode -> selectNode(node) }
            )
    }
    val flatXmlTree = flatXmlTreeMap.map { it.values.toList() }

    val selectedNodeIndex = combine(flatXmlTreeMap, selectedNode) { flatXmlTreeMap, selectedNode ->
        flatXmlTreeMap.keys.indexOf(selectedNode)
    }
    val activateScroll = combine(inspectorState, isNodeSelected) { inspectorState, isNodeSelected ->
        inspectorState == InspectorState.POPULATED && isNodeSelected
    }

    val showSelectedNodeProperties = combine(showXmlTree, isNodeSelected) { showXmlTree, isNodeSelected ->
        showXmlTree && isNodeSelected
    }
    val selectedNodePropertyMap = selectedNode.map { it.propertyMap }
}