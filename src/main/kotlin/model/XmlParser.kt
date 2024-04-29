package model

import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

object XmlParser {
    fun parseNode(element: Element): Node {
        val children = mutableListOf<Node>()
        // Iterate through all child nodes of the element
        for (i in 0 until element.childNodes.length) {
            val child = element.childNodes.item(i)
            // Check if the child is an Element and a "node"
            if (child is Element && child.tagName == "node") {
                children.add(parseNode(child))
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

    fun parseWindow(element: Element): Window {
        val nodes = mutableListOf<Node>()
        val hierarchy = element.getElementsByTagName("hierarchy").item(0)
        // Iterate through all child nodes of the window element
        for (i in 0 until hierarchy.childNodes.length) {
            val child = hierarchy.childNodes.item(i)
            // Check if the child is an Element and a "node"
            if (child is Element && child.tagName == "node") {
                nodes.add(parseNode(child))
            }
        }
        return Window(
            index = element.getAttribute("index").toInt(),
            type = element.getAttribute("type"),
            layer = element.getAttribute("layer").toInt(),
            token = element.getAttribute("token"),
            focused = element.getAttribute("focused").toBoolean(),
            nodes = nodes
        )
    }

    fun parseDisplay(element: Element): Display {
        val windows = mutableListOf<Window>()
        val windowElements = element.getElementsByTagName("window")
        for (i in 0 until windowElements.length) {
            windows.add(parseWindow(windowElements.item(i) as Element))
        }
        return Display(
            id = element.getAttribute("id").toInt(),
            windows = windows
        )
    }

    fun parseSystem(file: File): System {
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val doc = dBuilder.parse(file)
        doc.documentElement.normalize()
        val displayElements = doc.getElementsByTagName("display")
        val displays = mutableListOf<Display>()
        for (i in 0 until displayElements.length) {
            displays.add(parseDisplay(displayElements.item(i) as Element))
        }
        return System(displays)
    }
}