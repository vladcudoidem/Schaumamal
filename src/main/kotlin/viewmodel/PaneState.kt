package viewmodel

import oldModel.InspectorState
import oldModel.parser.xmlElements.Node
import oldModel.parser.xmlElements.System
import viewmodel.extraUiLogic.getFlatXmlTreeMap
import viewmodel.extraUiLogic.propertyMap

class PaneState(
    private val getInspectorState: () -> InspectorState,
    private val getDataRoot: () -> System,
    private val isNodeSelected: () -> Boolean,
    private val getSelectedNode: () -> Node,
    private val selectNode: (Node) -> Unit
) {
    val showXmlTree get() = getInspectorState() == InspectorState.POPULATED
    private val flatXmlTreeMap
        get() = getDataRoot().getFlatXmlTreeMap(
            selectedNode = getSelectedNode(),
            onNodeTreeLineClicked = { node: Node -> selectNode(node) }
        )
    val flatXmlTree get() = flatXmlTreeMap.values.toList()

    val selectedNodeIndex get() = flatXmlTreeMap.keys.indexOf(getSelectedNode())
    val activateScroll get() = getInspectorState() == InspectorState.POPULATED && isNodeSelected()

    val showSelectedNodeProperties get() = showXmlTree && isNodeSelected()
    val selectedNodePropertyMap get() = getSelectedNode().propertyMap
}