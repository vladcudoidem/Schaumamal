package model.parser

import model.parser.xmlElements.DisplayNode
import model.parser.xmlElements.GenericNode
import model.parser.xmlElements.WindowNode
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

class XmlParser(
    // Todo: remove default parameter.
    private val documentBuilder: DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
) {

    fun parseSystem(file: File): List<DisplayNode> {
        val document = documentBuilder.parse(file)
        document.documentElement.normalize()

        val displayNodes = mutableListOf<DisplayNode>()
        val displayElements = document.getElementsByTagName(NodeName.DISPLAY)
            // deep search for display because displays can only be direct children of a system node
        displayElements.forEach {
            displayNodes.add(parseDisplay(it))
        }

        return displayNodes
    }

    private fun parseDisplay(element: Element): DisplayNode {
        val windowNodes = mutableListOf<WindowNode>()
        val windowElements = element.getElementsByTagName(NodeName.WINDOW)
            // deep search for window because windows can only be direct children of a display node
        windowElements.forEach {
            windowNodes.add(parseWindow(it))
        }

        return DisplayNode(
            id = element.getAttribute(PropertyName.Display.ID),
            children = windowNodes
        )
    }

    private fun parseWindow(element: Element): WindowNode {
        val genericNodes = mutableListOf<GenericNode>()
        val hierarchyElement = element.getElementsByTagName(NodeName.HIERARCHY).item(0)
            // There is only one hierarchy tag in a window node (it is its child). The hierarchy tag has a rotation
            // property that we choose not to represent separately.
        val nodeElements = hierarchyElement.childNodes
            // We only search for children with a "node" tag in order to traverse the hierarchy one level at a time.
        nodeElements.forEach {
            if (it.tagName == NodeName.NODE) {
                genericNodes.add(parseNode(it))
            }
        }

        return with(element) {
            WindowNode(
                index = getAttribute(PropertyName.Window.INDEX).toInt(),
                id = getAttribute(PropertyName.Window.ID),
                title = getAttribute(PropertyName.Window.TITLE),
                bounds = getAttribute(PropertyName.Window.BOUNDS),
                active = getAttribute(PropertyName.Window.ACTIVE).toBoolean(),
                type = getAttribute(PropertyName.Window.TYPE),
                layer = getAttribute(PropertyName.Window.LAYER).toInt(),
                focused = getAttribute(PropertyName.Window.FOCUSED).toBoolean(),
                accessibilityFocused = getAttribute(PropertyName.Window.ACCESSIBILITY_FOCUSED).toBoolean(),
                children = genericNodes
            )
        }
    }

    private fun parseNode(element: Element): GenericNode {
        val children = mutableListOf<GenericNode>()
        // We only search for children with a "node" tag in order to traverse the hierarchy one level at a time.
        element.childNodes.forEach {
            if (it.tagName == NodeName.NODE) {
                children.add(parseNode(it))
            }
        }

        return with(element) {
            GenericNode(
                index = getAttribute(PropertyName.Node.INDEX).toInt(),
                text = getAttribute(PropertyName.Node.TEXT),
                resourceId = getAttribute(PropertyName.Node.RESOURCE_ID),
                className = getAttribute(PropertyName.Node.CLASS),
                packageName = getAttribute(PropertyName.Node.PACKAGE),
                contentDesc = getAttribute(PropertyName.Node.CONTENT_DESCRIPTION),
                checkable = getAttribute(PropertyName.Node.CHECKABLE).toBoolean(),
                checked = getAttribute(PropertyName.Node.CHECKED).toBoolean(),
                clickable = getAttribute(PropertyName.Node.CLICKABLE).toBoolean(),
                enabled = getAttribute(PropertyName.Node.ENABLED).toBoolean(),
                focusable = getAttribute(PropertyName.Node.FOCUSABLE).toBoolean(),
                focused = getAttribute(PropertyName.Node.FOCUSED).toBoolean(),
                scrollable = getAttribute(PropertyName.Node.SCROLLABLE).toBoolean(),
                longClickable = getAttribute(PropertyName.Node.LONG_CLICKABLE).toBoolean(),
                password = getAttribute(PropertyName.Node.PASSWORD).toBoolean(),
                selected = getAttribute(PropertyName.Node.SELECTED).toBoolean(),
                bounds = getAttribute(PropertyName.Node.BOUNDS),
                children = children
            )
        }
    }

    private fun NodeList.forEach(action: (Element) -> Unit) {
        for (i in 0 until length) {
            action(item(i) as Element)
        }
    }
}