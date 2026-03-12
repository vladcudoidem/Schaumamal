package view.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import model.parser.dataClasses.Bounds

fun Bounds.toOffset(): Offset = Offset(x = x, y = y)

fun Bounds.toSize(): Size = Size(width = width, height = height)

val Bounds.displayRepresentation: String
    get() = "x=${x.toInt()} y=${y.toInt()}, width=${width.toInt()} height=${height.toInt()}"
