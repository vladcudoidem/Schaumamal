package viewmodel

import shared.xmlElements.Node

val Node.propertyMap
    get() = linkedMapOf(
        "index" to index.toString(), 
        "text" to text,
        "resource-id" to resourceId,
        "class" to className,
        "package" to packageName,
        "content-desc" to contentDesc,
        "checkable" to checkable.toString(),
        "checked" to checked.toString(),
        "clickable" to clickable.toString(),
        "enabled" to enabled.toString(),
        "focusable" to focusable.toString(),
        "focused" to focused.toString(),
        "scrollable" to scrollable.toString(),
        "long-clickable" to longClickable.toString(),
        "password" to password.toString(),
        "selected" to selected.toString(),
        "bounds" to bounds
    ).mapValues { (_, value) -> value.ifEmpty { "-" } }.toMap(LinkedHashMap())