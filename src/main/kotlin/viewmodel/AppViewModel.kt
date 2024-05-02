package viewmodel

import model.InspectorState
import model.LayoutInspector
import model.parser.Node

// TODO create folders at startup if needed

class AppViewModel {
    private val layoutInspector = LayoutInspector()

    val isInspectorPopulated
        get() = layoutInspector.state == InspectorState.POPULATED
    val isInspectorWaiting
        get() = layoutInspector.state == InspectorState.WAITING
    val layoutData
        get() = layoutInspector.data

    val isNodeSelected
        get() = layoutInspector.isNodeSelected
    val selectedNode
        get() = layoutInspector.selectedNode

    fun extractLayout() = layoutInspector.extractLayout()

    fun selectNode(node: Node) = layoutInspector.selectNode(node)

    fun teardown() {
        layoutInspector.teardown()
    }
}