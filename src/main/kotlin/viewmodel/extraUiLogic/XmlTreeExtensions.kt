package viewmodel.extraUiLogic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import model.parser.xmlElements.Display
import model.parser.xmlElements.Node
import model.parser.xmlElements.System
import model.parser.xmlElements.Window
import model.parser.xmlElements.XmlElement
import shared.Colors.highlightColor
import viewmodel.XmlTreeLine

fun System.getFlatXmlTreeMap(
    selectedNode: Node,
    onNodeTreeLineClicked: (Node) -> Unit
): LinkedHashMap<XmlElement, XmlTreeLine> {
    val result: LinkedHashMap<XmlElement, XmlTreeLine> = linkedMapOf()

    forThisAndDescendants { element, depth ->

        result += when (element) {

            is System -> element to XmlTreeLine(
                text = buildAnnotatedString {
                    append("System {displays=${element.children.size}}")
                },
                textBackgroundColor = Color.Transparent,
                depth = depth,
                onClickText = { }
            )
            is Display -> element to XmlTreeLine(
                text = buildAnnotatedString {
                    append("Display {id=${element.id}, windows=${element.children.size}}")
                },
                textBackgroundColor = Color.Transparent,
                depth = depth,
                onClickText = { }
            )

            is Window -> element to XmlTreeLine(
                text = buildAnnotatedString {
                    append("(${element.index}) Window {title=\"${element.title}\"} ${element.bounds}")
                },
                textBackgroundColor = Color.Transparent,
                depth = depth,
                onClickText = { }
            )

            is Node -> element to XmlTreeLine(
                text = buildAnnotatedString {
                    val formattedClassName = element.className.split(".").last()
                    val formattedResourceId = element.resourceId.split(":").last().let {
                        // Add a delimiter if node has a resource-id
                        if (it.isEmpty()) it
                        else "$it "
                    }
                    val formattedText = element.text.take(10) + if (element.text.length > 10) "..." else ""

                    val text = "(${element.index}) $formattedClassName $formattedResourceId" +
                        "{text=\"$formattedText\", contDesc=\"${element.contentDesc}\"} ${element.bounds}"

                    append(text)
                },
                textBackgroundColor = if (selectedNode === element) {
                    highlightColor
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