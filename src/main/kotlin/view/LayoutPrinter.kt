package view

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import model.Node
import model.SystemNode

object LayoutPrinter {
    @Composable
    fun getStructure(root: SystemNode) {
        Text("System")
        for (display in root.displayNodes) {
            Text("   Display")
            for (window in display.windowNodes) {
                Text("      Window")
                for (node in window.nodes) {
                    printNodes(node, "         ")
                }
            }
        }
    }

    @Composable
    fun printNodes(node: Node, prefix: String) {
        Text("${prefix}Node text=${node.text} resId=${node.resourceId}")
        for (childNode in node.children) {
            printNodes(childNode, "   $prefix")
        }
    }
}