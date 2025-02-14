package view.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import model.parser.dataClasses.DisplayNode
import model.parser.dataClasses.GenericNode
import model.parser.dataClasses.Node
import model.parser.dataClasses.WindowNode
import shared.Colors.accentColor
import shared.Colors.discreteTextColor
import view.panes.XmlTreeLine

fun DisplayNode.getFlatXmlTreeMap(
    selectedNode: GenericNode,
    onNodeTreeLineClicked: (GenericNode) -> Unit
): LinkedHashMap<Node, XmlTreeLine> {
    val result: LinkedHashMap<Node, XmlTreeLine> = linkedMapOf()

    forThisAndDescendants { element, depth ->

        result += when (element) {

            is DisplayNode -> element to XmlTreeLine(
                text = element.displayText,
                textBackgroundColor = Color.Transparent,
                depth = depth,
                onClickText = { }
            )

            is WindowNode -> element to XmlTreeLine(
                text = element.displayText,
                textBackgroundColor = Color.Transparent,
                depth = depth,
                onClickText = { }
            )

            is GenericNode -> element to XmlTreeLine(
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

private val Node.displayText: AnnotatedString
    get() = when (this) {

        is DisplayNode -> buildAnnotatedString {
            withStyle(style = SpanStyle(color = discreteTextColor)) {
                append("Display")

                append(" {")
                append(" id=$id")
                append(" windows=${children.size}")
                append(" }")
            }
        }

        is WindowNode -> buildAnnotatedString {
            withStyle(style = SpanStyle(color = discreteTextColor)) {
                append("($index)")
                append(" Window")

                append(" {")
                append(" title=\"$title\"")
                append(" }")

                append(" $bounds")
            }
        }

        is GenericNode -> buildAnnotatedString {
            append("($index)")
            val formattedClassName = className.split(".").last()
            append(" $formattedClassName")

            if (resourceId.isNotEmpty()) {
                val formattedResourceId = resourceId.split(":").last()
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    )
                ) {
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

private fun Node.forThisAndDescendants(
    depth: Int = 0,
    action: (element: Node, depth: Int) -> Unit
) {
    action(this, depth)

    children.forEach {
        it.forThisAndDescendants(depth = depth + 1, action = action)
    }
}