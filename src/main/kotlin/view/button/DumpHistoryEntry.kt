package view.button

import androidx.compose.ui.graphics.ImageBitmap

data class DumpHistoryEntry(
    val selected: Boolean,
    val thumbnail: ImageBitmap,
    val name: String,
    val date: String,
    val displayCount: Int,
    val onDumpHistoryEntryClicked: () -> Unit
)
