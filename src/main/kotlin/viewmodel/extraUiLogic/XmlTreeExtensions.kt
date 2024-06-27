package viewmodel.extraUiLogic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import model.parser.xmlElements.Display
import model.parser.xmlElements.Node
import model.parser.xmlElements.System
import model.parser.xmlElements.Window
import model.parser.xmlElements.XmlElement
import shared.Colors
import shared.Colors.accentColor
import viewmodel.XmlTreeLine

fun System.getFlatXmlTreeMap(
    selectedNode: Node,
    onNodeTreeLineClicked: (Node) -> Unit
): LinkedHashMap<XmlElement, XmlTreeLine> {
    val result: LinkedHashMap<XmlElement, XmlTreeLine> = linkedMapOf()

    forThisAndDescendants { element, depth ->

        result += when (element) {

            is System -> element to XmlTreeLine(
                text = element.displayText,
                textBackgroundColor = Color.Transparent,
                depth = depth,
                onClickText = { }
            )

            is Display -> element to XmlTreeLine(
                text = element.displayText,
                textBackgroundColor = Color.Transparent,
                depth = depth,
                onClickText = { }
            )

            is Window -> element to XmlTreeLine(
                text = element.displayText,
                textBackgroundColor = Color.Transparent,
                depth = depth,
                onClickText = { }
            )

            is Node -> element to XmlTreeLine(
                text = element.displayText,
                textBackgroundColor = if (selectedNode === element) {
                    accentColor
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

private val XmlElement.displayText: AnnotatedString
    get() = when (this) {

        is System -> buildAnnotatedString {
            withStyle(style = SpanStyle(color = Colors.discreteTextColor)) {
                append("System")

                append(" {")
                append(" displays=${children.size}")
                append(" }")
            }
        }

        is Display -> buildAnnotatedString {
            withStyle(style = SpanStyle(color = Colors.discreteTextColor)) {
                append("Display")

                append(" {")
                append(" id=$id")
                append(" windows=${children.size}")
                append(" }")
            }
        }

        is Window -> buildAnnotatedString {
            withStyle(style = SpanStyle(color = Colors.discreteTextColor)) {
                append("($index)")
                append(" Window")

                append(" {")
                append(" title=\"$title\"")
                append(" }")

                append(" $bounds")
            }
        }

        is Node -> buildAnnotatedString {
            append("($index)")
            val formattedClassName = className.split(".").last()
            append(" $formattedClassName")

            if (resourceId.isNotEmpty()) {
                val formattedResourceId = resourceId.split(":").last()
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)) {
                    append(" $formattedResourceId")
                }
            }

            if (text.isNotEmpty()) {
                append(" {")
                val formattedText = text.take(10) + if (text.length > 10) "..." else ""
                append(" text=\"$formattedText\"")
                append(" }")
            }

            append(" $bounds")

            if (contentDesc.isNotEmpty()) {
                append(" -")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(" \"$contentDesc\"")
                }
            }
        }

        else -> error("displayText template not specified for this XmlElement subtype.")
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