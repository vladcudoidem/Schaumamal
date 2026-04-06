package view.utils

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
import shared.Colors.discreteTextColor
import view.panes.XmlTreeLine

fun DisplayNode.getFlatXmlTreeMap(
    onNodeTreeLineClicked: (GenericNode) -> Unit
): LinkedHashMap<Node, XmlTreeLine> {

    val flattenedTreeMap =
        flattenTo<Pair<Node, XmlTreeLine>> { currentNode, depth, parentProduct ->
            val hasChildren = currentNode.children.isNotEmpty()

            val currentProduct =
                when (currentNode) {
                    is DisplayNode ->
                        currentNode to
                            XmlTreeLine(
                                text = currentNode.displayText,
                                depth = depth,
                                onClickText = {},
                                parentLine = parentProduct?.second,
                                // It makes no sense to collapse display node.
                                isCollapsible = false,
                            )

                    is WindowNode ->
                        currentNode to
                            XmlTreeLine(
                                text = currentNode.displayText,
                                depth = depth,
                                onClickText = {},
                                parentLine = parentProduct?.second,
                                isCollapsible = hasChildren,
                            )

                    is GenericNode ->
                        currentNode to
                            XmlTreeLine(
                                text = currentNode.displayText,
                                depth = depth,
                                onClickText = { onNodeTreeLineClicked(currentNode) },
                                parentLine = parentProduct?.second,
                                isCollapsible = hasChildren,
                            )

                    else -> error("XmlTreeLine template not specified for this XmlElement subtype.")
                }

            currentProduct
        }

    return flattenedTreeMap.toMap(LinkedHashMap())
}

private val Node.displayText: AnnotatedString
    get() =
        when (this) {
            is DisplayNode ->
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = discreteTextColor)) {
                        append("Display")

                        append(" {")
                        append(" id=$id")
                        append(" windows=${children.size}")
                        append(" }")
                    }
                }

            is WindowNode ->
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = discreteTextColor)) {
                        append("($index)")
                        append(" Window")

                        append(" {")
                        append(" title=\"$title\"")
                        append(" }")

                        append(" $bounds")
                    }
                }

            is GenericNode ->
                buildAnnotatedString {
                    append("($index)")
                    val formattedClassName = className.split(".").last()
                    append(" $formattedClassName")

                    if (resourceId.isNotEmpty()) {
                        val formattedResourceId = resourceId.split(":").last()
                        withStyle(
                            style =
                                SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Italic,
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

                    if (contentDesc.isNotEmpty()) {
                        append(" -")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(" \"$contentDesc\"")
                        }
                    }
                }

            else -> error("displayText template not specified for this XmlElement subtype.")
        }

private fun <ProductType> Node.flattenTo(
    depth: Int = 0,
    parentProduct: ProductType? = null,
    generateProduct: (currentNode: Node, depth: Int, parentProduct: ProductType?) -> ProductType,
): List<ProductType> {
    val result = mutableListOf<ProductType>()

    val currentProduct = generateProduct(this, depth, parentProduct)
    result.add(currentProduct)

    children.forEach {
        val childResult =
            it.flattenTo(depth = depth + 1, parentProduct = currentProduct, generateProduct)
        result += childResult
    }

    return result
}
