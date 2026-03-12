package view.utils

import model.parser.PropertyInfo
import model.parser.dataClasses.GenericNode

val GenericNode.propertyMap
    get() =
        linkedMapOf(
                PropertyInfo.Node.Index.displayName to index.toString(),
                PropertyInfo.Node.Text.displayName to text,
                PropertyInfo.Node.ResourceId.displayName to resourceId,
                PropertyInfo.Node.Class.displayName to className,
                PropertyInfo.Node.Package.displayName to packageName,
                PropertyInfo.Node.ContentDescription.displayName to contentDesc,
                PropertyInfo.Node.Checkable.displayName to checkable.toString(),
                PropertyInfo.Node.Checked.displayName to checked.toString(),
                PropertyInfo.Node.Clickable.displayName to clickable.toString(),
                PropertyInfo.Node.Enabled.displayName to enabled.toString(),
                PropertyInfo.Node.Focusable.displayName to focusable.toString(),
                PropertyInfo.Node.Focused.displayName to focused.toString(),
                PropertyInfo.Node.Scrollable.displayName to scrollable.toString(),
                PropertyInfo.Node.LongClickable.displayName to longClickable.toString(),
                PropertyInfo.Node.Password.displayName to password.toString(),
                PropertyInfo.Node.Selected.displayName to selected.toString(),
                PropertyInfo.Node.Bounds.displayName to bounds.displayRepresentation,
            )
            .mapValues { (_, value) -> value.ifEmpty { "-" } }
            .toMap(LinkedHashMap())
