package model.parser

// Todo: move these to the respective data classes.

data class PropertyInfo(val rawName: String, val displayName: String) {
    object Display {
        val Id = PropertyInfo(rawName = "id", displayName = "ID")
    }

    // Todo: add support for selecting window nodes
    object Window {
        val Index = PropertyInfo(rawName = "index", displayName = "Index")
        val Id = PropertyInfo(rawName = "id", displayName = "ID")
        val Title = PropertyInfo(rawName = "title", displayName = "Title")
        val Bounds = PropertyInfo(rawName = "bounds", displayName = "Bounds")
        val Active = PropertyInfo(rawName = "active", displayName = "Active")
        val Type = PropertyInfo(rawName = "type", displayName = "Type")
        val Layer = PropertyInfo(rawName = "layer", displayName = "Layer")
        val Focused = PropertyInfo(rawName = "focused", displayName = "Focused")
        val AccessibilityFocused =
            PropertyInfo(rawName = "accessibility-focused", displayName = "Accessibility focused")
    }

    object Node {
        val Index = PropertyInfo(rawName = "index", displayName = "Index")
        val Text = PropertyInfo(rawName = "text", displayName = "Text")
        val ResourceId = PropertyInfo(rawName = "resource-id", displayName = "Resource ID")
        val Class = PropertyInfo(rawName = "class", displayName = "Class")
        val Package = PropertyInfo(rawName = "package", displayName = "Package")
        val ContentDescription =
            PropertyInfo(rawName = "content-desc", displayName = "Content description")
        val Checkable = PropertyInfo(rawName = "checkable", displayName = "Checkable")
        val Checked = PropertyInfo(rawName = "checked", displayName = "Checked")
        val Clickable = PropertyInfo(rawName = "clickable", displayName = "Clickable")
        val Enabled = PropertyInfo(rawName = "enabled", displayName = "Enabled")
        val Focusable = PropertyInfo(rawName = "focusable", displayName = "Focusable")
        val Focused = PropertyInfo(rawName = "focused", displayName = "Focused")
        val Scrollable = PropertyInfo(rawName = "scrollable", displayName = "Scrollable")
        val LongClickable = PropertyInfo(rawName = "long-clickable", displayName = "Long clickable")
        val Password = PropertyInfo(rawName = "password", displayName = "Password")
        val Selected = PropertyInfo(rawName = "selected", displayName = "Selected")
        val Bounds = PropertyInfo(rawName = "bounds", displayName = "Bounds")
    }
}
