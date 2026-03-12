package viewmodel

import model.parser.dataClasses.Bounds

fun Bounds.toSearchableArray(): Array<String> =
    listOf(x, y, width, height).map { it.toString() }.toTypedArray()
