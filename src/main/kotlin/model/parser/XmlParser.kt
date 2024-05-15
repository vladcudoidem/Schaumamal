package model.parser

import shared.xmlElements.Display
import shared.xmlElements.Node
import shared.xmlElements.System
import shared.xmlElements.Window
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

object XmlParser {

    fun parseSystem(file: File): System {
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val doc = dBuilder.parse(file)
        doc.documentElement.normalize()

        val displays = mutableListOf<Display>()
        val displayElements = doc.getElementsByTagName("display")
            // deep search for display because displays can only be direct children of a system node
        displayElements.forEach {
            displays.add(parseDisplay(it))
        }

        return System(children = displays)
    }

    private fun parseDisplay(element: Element): Display {
        val windows = mutableListOf<Window>()
        val windowElements = element.getElementsByTagName("window")
            // deep search for window because windows can only be direct children of a display node
        windowElements.forEach {
            windows.add(parseWindow(it))
        }

        return Display(
            id = element.getAttribute("id").toInt(),
            children = windows
        )
    }

    private fun parseWindow(element: Element): Window {
        val hierarchyElement = element.getElementsByTagName("hierarchy").item(0)
            // There is only one hierarchy tag in a window node, and it is its direct child. The hierarchy tag has a
            // rotation property that we choose to ignore.

        val nodes = mutableListOf<Node>()
        hierarchyElement.childNodes.forEach {
            if (it.tagName == "node") {
                nodes.add(parseNode(it))
            }
        }

        return Window(
            index = element.getAttribute("index").toInt(),
            id = element.getAttribute("id").toInt(),
            title = element.getAttribute("title"),
            bounds = element.getAttribute("bounds"),
            active = element.getAttribute("active").toBoolean(),
            type = element.getAttribute("type"),
            layer = element.getAttribute("layer").toInt(),
            token = element.getAttribute("token"),
            focused = element.getAttribute("focused").toBoolean(),
            accessibilityFocused = element.getAttribute("accessibility-focused").toBoolean(),
            children = nodes
        )
    }

    private fun parseNode(element: Element): Node {
        val children = mutableListOf<Node>()
        element.childNodes.forEach {
            if (it.tagName == "node") {
                children.add(parseNode(it))
            }
        }

        return Node(
            index = element.getAttribute("index").toInt(),
            text = element.getAttribute("text"),
            resourceId = element.getAttribute("resource-id"),
            className = element.getAttribute("class"),
            packageName = element.getAttribute("package"),
            contentDesc = element.getAttribute("content-desc"),
            checkable = element.getAttribute("checkable").toBoolean(),
            checked = element.getAttribute("checked").toBoolean(),
            clickable = element.getAttribute("clickable").toBoolean(),
            enabled = element.getAttribute("enabled").toBoolean(),
            focusable = element.getAttribute("focusable").toBoolean(),
            focused = element.getAttribute("focused").toBoolean(),
            scrollable = element.getAttribute("scrollable").toBoolean(),
            longClickable = element.getAttribute("long-clickable").toBoolean(),
            password = element.getAttribute("password").toBoolean(),
            selected = element.getAttribute("selected").toBoolean(),
            bounds = element.getAttribute("bounds"),
            children = children
        )
    }

    private fun NodeList.forEach(action: (Element) -> Unit) {
        for (i in 0 until length) {
            action(item(i) as Element)
        }
    }
}