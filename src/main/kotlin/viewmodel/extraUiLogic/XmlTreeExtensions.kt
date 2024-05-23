package viewmodel.extraUiLogic

import androidx.compose.ui.graphics.Color
import model.parser.xmlElements.Display
import model.parser.xmlElements.Node
import model.parser.xmlElements.System
import model.parser.xmlElements.Window
import model.parser.xmlElements.XmlElement
import viewmodel.Colors.highlightedTextBackgroundColor
import viewmodel.XmlTreeLine

fun System.getFlatXmlTreeMap(
    selectedNode: Node,
    onNodeTreeLineClicked: (Node) -> Unit
): LinkedHashMap<XmlElement, XmlTreeLine> {
    val result: LinkedHashMap<XmlElement, XmlTreeLine> = linkedMapOf()

    forThisAndDescendants { element, depth ->

        result += when (element) {

            is System -> element to XmlTreeLine(
                text = "System {displays=${element.children.size}}",
                textBackgroundColor = Color.Transparent,
                depth = depth,
                onClickText = { }
            )

            is Display -> element to XmlTreeLine(
                text = "Display {id=${element.id}, windows=${element.children.size}}",
                textBackgroundColor = Color.Transparent,
                depth = depth,
                onClickText = { }
            )

            is Window -> element to XmlTreeLine(
                text = "(${element.index}) Window {title=\"${element.title}\"} ${element.bounds}",
                textBackgroundColor = Color.Transparent,
                depth = depth,
                onClickText = { }
            )

            is Node -> element to XmlTreeLine(
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
                onClickText = { onNodeTreeLineClicked(element) }
            )

            else -> error("XmlTreeLine template not specified for this XmlElement subtype.")
        }

    }

    return result
}

private fun XmlElement.forThisAndDescendants(
    depth: Int = 0,
    action: (element: XmlElement, depth: Int) -> Unit
) {
    action(this, depth)

    children.forEach {
        it.forThisAndDescendants(depth = depth + 1, action = action)
    }
}