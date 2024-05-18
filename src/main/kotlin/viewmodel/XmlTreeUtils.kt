package viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import shared.XmlTreeLine
import shared.xmlElements.Display
import shared.xmlElements.Node
import shared.xmlElements.System
import shared.xmlElements.Window
import shared.xmlElements.XmlElement
import view.Colors.highlightedTextBackgroundColor

fun System.getFlatXmlTree(
    selectedNode: Node,
    onNodeTreeLineClicked: (Node) -> Unit,
    onNodeTreeLineGloballyPositioned: (LayoutCoordinates, Node) -> Unit
): List<XmlTreeLine> {
    val result: MutableList<XmlTreeLine> = mutableListOf()

    forThisAndDescendants { element, depth ->

        result += when (element) {

            is System -> XmlTreeLine(
                text = "System {displays=${element.children.size}}",
                textBackgroundColor = Color.Transparent,
                depth = depth,
                onClickText = { },
                onTreeLineGloballyPositioned = { }
            )

            is Display -> XmlTreeLine(
                text = "Display {id=${element.id}, windows=${element.children.size}}",
                textBackgroundColor = Color.Transparent,
                depth = depth,
                onClickText = { },
                onTreeLineGloballyPositioned = { }
            )

            is Window -> XmlTreeLine(
                text = "(${element.index}) Window {title=\"${element.title}\"} ${element.bounds}",
                textBackgroundColor = Color.Transparent,
                depth = depth,
                onClickText = { },
                onTreeLineGloballyPositioned = { }
            )

            is Node -> XmlTreeLine(
                text = run {
                    val formattedClassName = element.className.split(".").last()
                    val formattedResourceId = element.resourceId.split(":").last().let {
                        // Add a delimiter if node has a resource-id
                        if (it.isEmpty()) it
                        else "$it "
                    }
                    val formattedText = element.text.take(10) + if (element.text.length > 10) "..." else ""

                    "(${element.index}) $formattedClassName $formattedResourceId" +
                            "{text=\"$formattedText\", contDesc=\"${element.contentDesc}\"} ${element.bounds}"
                },
                textBackgroundColor = if (selectedNode === element) {
                    highlightedTextBackgroundColor
                } else {
                    Color.Transparent
                },
                depth = depth,
                onClickText = { onNodeTreeLineClicked(element) },
                onTreeLineGloballyPositioned = { layoutCoordinates: LayoutCoordinates ->
                    onNodeTreeLineGloballyPositioned(layoutCoordinates, element)
                }
            )

            else -> error("XmlTreeLine template not specified for this XmlElement subtype.")
        }

    }

    return result
}

fun XmlElement.forThisAndDescendants(
    depth: Int = 0,
    action: (element: XmlElement, depth: Int) -> Unit
) {
    action(this, depth)

    children.forEach {
        it.forThisAndDescendants(depth = depth + 1, action = action)
    }
}