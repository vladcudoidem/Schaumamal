package viewmodel

import model.LayoutInspector
import model.parser.Node

class AppViewModel {
    private val layoutInspector = LayoutInspector()

    val inspectorState get() = layoutInspector.state
    val layoutData get() = layoutInspector.data

    val isNodeSelected get() = layoutInspector.isNodeSelected
    val selectedNode get() = layoutInspector.selectedNode

    fun extractLayout() = layoutInspector.extractLayout()

    fun selectNode(node: Node) = layoutInspector.selectNode(node)

    fun teardown() {
        layoutInspector.teardown()
    }
}