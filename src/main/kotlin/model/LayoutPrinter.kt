package model

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

object LayoutPrinter {
    @Composable
    fun getStructure(root: System) {
        Text("System")
        for (display in root.displays) {
            Text("   Display")
            for (window in display.windows) {
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