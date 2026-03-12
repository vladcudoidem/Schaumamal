package model.parser

import java.io.File
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import model.parser.dataClasses.Bounds
import model.parser.dataClasses.DisplayNode
import model.parser.dataClasses.GenericNode
import model.parser.dataClasses.WindowNode
import org.w3c.dom.Element
import org.w3c.dom.NodeList

class XmlParser(
    // Todo: remove default parameter.
    private val documentBuilder: DocumentBuilder =
        DocumentBuilderFactory.newInstance().newDocumentBuilder()
) {

    fun parseSystem(file: File): List<DisplayNode> {
        val document = documentBuilder.parse(file)
        document.documentElement.normalize()

        val displayNodes = mutableListOf<DisplayNode>()
        val displayElements = document.getElementsByTagName(NodeName.DISPLAY)
        // deep search for display because displays can only be direct children of a system node
        displayElements.forEach { displayNodes.add(parseDisplay(it)) }

        return displayNodes
    }

    private fun parseDisplay(element: Element): DisplayNode {
        val windowNodes = mutableListOf<WindowNode>()
        val windowElements = element.getElementsByTagName(NodeName.WINDOW)
        // deep search for window because windows can only be direct children of a display node
        windowElements.forEach { windowNodes.add(parseWindow(it)) }

        return DisplayNode(
            id = element.getAttribute(PropertyInfo.Display.Id.rawName),
            children = windowNodes,
        )
    }

    private fun parseWindow(element: Element): WindowNode {
        val genericNodes = mutableListOf<GenericNode>()
        val hierarchyElement = element.getElementsByTagName(NodeName.HIERARCHY).item(0)
        // There is only one hierarchy tag in a window node (it is its child). The hierarchy tag has
        // a rotation
        // property that we choose not to represent separately.
        val nodeElements = hierarchyElement.childNodes
        // We only search for children with a "node" tag in order to traverse the hierarchy one
        // level at a time.
        nodeElements.forEach {
            if (it.tagName == NodeName.NODE) {
                genericNodes.add(parseNode(it))
            }
        }

        return with(element) {
            WindowNode(
                index = getAttribute(PropertyInfo.Window.Index.rawName).toInt(),
                id = getAttribute(PropertyInfo.Window.Id.rawName),
                title = getAttribute(PropertyInfo.Window.Title.rawName),
                bounds = getAttribute(PropertyInfo.Window.Bounds.rawName),
                active = getAttribute(PropertyInfo.Window.Active.rawName).toBoolean(),
                type = getAttribute(PropertyInfo.Window.Type.rawName),
                layer = getAttribute(PropertyInfo.Window.Layer.rawName).toInt(),
                focused = getAttribute(PropertyInfo.Window.Focused.rawName).toBoolean(),
                accessibilityFocused =
                    getAttribute(PropertyInfo.Window.AccessibilityFocused.rawName).toBoolean(),
                children = genericNodes,
            )
        }
    }

    private fun parseNode(element: Element): GenericNode {
        val children = mutableListOf<GenericNode>()
        // We only search for children with a "node" tag in order to traverse the hierarchy one
        // level at a time.
        element.childNodes.forEach {
            if (it.tagName == NodeName.NODE) {
                children.add(parseNode(it))
            }
        }

        return with(element) {
            GenericNode(
                index = getAttribute(PropertyInfo.Node.Index.rawName).toInt(),
                text = getAttribute(PropertyInfo.Node.Text.rawName),
                resourceId = getAttribute(PropertyInfo.Node.ResourceId.rawName),
                className = getAttribute(PropertyInfo.Node.Class.rawName),
                packageName = getAttribute(PropertyInfo.Node.Package.rawName),
                contentDesc = getAttribute(PropertyInfo.Node.ContentDescription.rawName),
                checkable = getAttribute(PropertyInfo.Node.Checkable.rawName).toBoolean(),
                checked = getAttribute(PropertyInfo.Node.Checked.rawName).toBoolean(),
                clickable = getAttribute(PropertyInfo.Node.Clickable.rawName).toBoolean(),
                enabled = getAttribute(PropertyInfo.Node.Enabled.rawName).toBoolean(),
                focusable = getAttribute(PropertyInfo.Node.Focusable.rawName).toBoolean(),
                focused = getAttribute(PropertyInfo.Node.Focused.rawName).toBoolean(),
                scrollable = getAttribute(PropertyInfo.Node.Scrollable.rawName).toBoolean(),
                longClickable = getAttribute(PropertyInfo.Node.LongClickable.rawName).toBoolean(),
                password = getAttribute(PropertyInfo.Node.Password.rawName).toBoolean(),
                selected = getAttribute(PropertyInfo.Node.Selected.rawName).toBoolean(),
                bounds =
                    run {
                        val boundsString = getAttribute(PropertyInfo.Node.Bounds.rawName)
                        Bounds.fromBoundsString(boundsString)
                    },
                children = children,
            )
        }
    }

    private fun NodeList.forEach(action: (Element) -> Unit) {
        for (i in 0 until length) {
            action(item(i) as Element)
        }
    }
}
