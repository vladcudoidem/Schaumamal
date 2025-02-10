package view.utils

import model.parser.PropertyName
import model.parser.dataClasses.GenericNode

val GenericNode.propertyMap
    get() = linkedMapOf(
        PropertyName.Node.INDEX to index.toString(),
        PropertyName.Node.TEXT to text,
        PropertyName.Node.RESOURCE_ID to resourceId,
        PropertyName.Node.CLASS to className,
        PropertyName.Node.PACKAGE to packageName,
        PropertyName.Node.CONTENT_DESCRIPTION to contentDesc,
        PropertyName.Node.CHECKABLE to checkable.toString(),
        PropertyName.Node.CHECKED to checked.toString(),
        PropertyName.Node.CLICKABLE to clickable.toString(),
        PropertyName.Node.ENABLED to enabled.toString(),
        PropertyName.Node.FOCUSABLE to focusable.toString(),
        PropertyName.Node.FOCUSED to focused.toString(),
        PropertyName.Node.SCROLLABLE to scrollable.toString(),
        PropertyName.Node.LONG_CLICKABLE to longClickable.toString(),
        PropertyName.Node.PASSWORD to password.toString(),
        PropertyName.Node.SELECTED to selected.toString(),
        PropertyName.Node.BOUNDS to bounds
    ).mapValues { (_, value) -> value.ifEmpty { "-" } }.toMap(LinkedHashMap())