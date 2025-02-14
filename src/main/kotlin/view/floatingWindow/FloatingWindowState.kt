package view.floatingWindow

import androidx.compose.ui.res.loadImageBitmap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import model.repository.dataClasses.Dump
import view.button.DumpHistoryEntry
import view.utils.getFormattedDate
import java.io.File
import java.io.FileInputStream

class FloatingWindowState(
    resolvedDumpThumbnails: Flow<Map<Dump, File>>,
    private val selectDump: (Dump) -> Unit
) {
    private val _windowState = MutableStateFlow(WindowState.HIDDEN)
    val windowState get() = _windowState.asStateFlow()

    val dumpHistoryEntries = resolvedDumpThumbnails.map {
        it.map { (dump, thumbnail) ->
            DumpHistoryEntry(
                thumbnail = loadImageBitmap(FileInputStream(thumbnail)), // Todo: refactor deprecated code.
                name = dump.nickname,
                date = getFormattedDate(dump.timeMilliseconds),
                displayCount = dump.displays.size,
                onDumpHistoryEntryClicked = {
                    closeFloatingWindow()
                    selectDump(dump)
                }
            )
        }
    }

    fun openDumpHistory() {
        _windowState.value = WindowState.SHOW_DUMP_HISTORY
    }

    fun closeFloatingWindow() {
        _windowState.value = WindowState.HIDDEN
    }
}