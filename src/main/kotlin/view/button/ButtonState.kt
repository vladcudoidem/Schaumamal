package view.button

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import model.InspectorState
import model.dumper.DumpProgressHandler
import model.repository.dataClasses.Dump
import view.utils.getFormattedDate
import viewmodel.Direction

class ButtonState(
    inspectorState: StateFlow<InspectorState>,
    selectedDump: StateFlow<Dump>,
    displayIndex: StateFlow<Int>,
    displayCount: StateFlow<Int>,
    private val extract: (DumpProgressHandler) -> Unit,
    private val switchDisplay: (Direction) -> Unit,
    private val openDumpHistory: () -> Unit,
) {
    val showDumpSuggestion = inspectorState.map { it == InspectorState.EMPTY }
    val dumpSuggestionText = "Smash the button."

    val showCurrentDump = inspectorState.map { it == InspectorState.POPULATED }
    val currentDumpInfo =
        selectedDump
            .onEach {
                // Reset the progress bar in the background when a new dump is made available.
                dumpProgress.value = 0f
            }
            .map { "${it.nickname} @ ${getFormattedDate(it.timeMilliseconds)}" }

    val showDumpProgress = inspectorState.map { it == InspectorState.WAITING }
    val dumpProgress = MutableStateFlow(0f)
    val dumpProgressText = MutableStateFlow("")

    val isExtractButtonEnabled = inspectorState.map { it != InspectorState.WAITING }

    val areDisplayControlButtonsEnabled = inspectorState.map { it == InspectorState.POPULATED }
    val displayCounter =
        combine(displayIndex, displayCount, inspectorState) {
            displayIndex,
            displayCount,
            inspectorState ->
            if (inspectorState == InspectorState.POPULATED) {
                "${displayIndex + 1}/$displayCount"
            } else {
                "?/?"
            }
        }

    val areResizeButtonsEnabled = inspectorState.map { it == InspectorState.POPULATED }

    val isOpenDumpHistoryButtonEnabled = inspectorState.map { it == InspectorState.POPULATED }

    fun onExtractButtonPressed() {
        extract(
            DumpProgressHandler { progress, message ->
                dumpProgress.value = progress
                dumpProgressText.value = message
            }
        )
    }

    fun onNextDisplayButtonPressed() {
        switchDisplay(Direction.NEXT)
    }

    fun onPreviousDisplayButtonPressed() {
        switchDisplay(Direction.PREVIOUS)
    }

    fun onOpenDumpHistoryButtonPressed() {
        openDumpHistory()
    }
}
