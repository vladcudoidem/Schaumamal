package view.utils

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Int.toDp(density: Float) = (this / density).dp

fun Float.toDp(density: Float) = (this / density).dp

fun Dp.toPx(density: Float): Float = value * density