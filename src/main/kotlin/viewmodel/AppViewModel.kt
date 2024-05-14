package viewmodel

import model.InspectorState
import model.LayoutInspector
import model.parser.Node

class AppViewModel {
    private val layoutInspector = LayoutInspector()

    val showButtonText get() = layoutInspector.state != InspectorState.POPULATED
    val buttonText get() = when(layoutInspector.state) {
        InspectorState.POPULATED -> ""
        InspectorState.EMPTY -> "...smash the red button"
        InspectorState.WAITING -> "...dumping"
    }

    fun onExtractButtonPressed() = layoutInspector.extractLayout()

    val inspectorState get() = layoutInspector.state
    val layoutData get() = layoutInspector.data

    val isNodeSelected get() = layoutInspector.isNodeSelected
    val selectedNode get() = layoutInspector.selectedNode

    fun selectNode(node: Node) = layoutInspector.selectNode(node)

    fun teardown() {
        layoutInspector.teardown()
    }
}