package viewmodel

import androidx.compose.ui.geometry.Size

val Size.Companion.Irrelevant: Size
    get() = Size(width = 1.0f, height = 1.0f)