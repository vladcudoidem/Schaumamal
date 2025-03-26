package view.floatingWindow

import androidx.compose.ui.res.loadImageBitmap
import java.io.File
import java.io.FileInputStream
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import model.repository.dataClasses.Dump
import view.button.DumpHistoryEntry
import view.utils.getFormattedDate

class FloatingWindowState(
    selectedDump: StateFlow<Dump>,
    resolvedDumpThumbnails: Flow<Map<Dump, File>>,
    private val selectDump: (Dump) -> Unit,
) {
    private val _windowState = MutableStateFlow(WindowState.HIDDEN)
    val windowState
        get() = _windowState.asStateFlow()

    val dumpHistoryEntries =
        combine(resolvedDumpThumbnails, selectedDump) { resolvedDumpThumbnails, selectedDump ->
            resolvedDumpThumbnails.map { (dump, thumbnail) ->
                DumpHistoryEntry(
                    selected = dump == selectedDump,
                    thumbnail =
                        loadImageBitmap(
                            FileInputStream(thumbnail)
                        ), // Todo: refactor deprecated code.
                    name = dump.nickname,
                    date = getFormattedDate(dump.timeMilliseconds),
                    displayCount = dump.displays.size,
                    onDumpHistoryEntryClicked = {
                        closeFloatingWindow()
                        selectDump(dump)
                    },
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
