package view.button

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
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
    private var previousInspectorState: InspectorState? = null
    private val transformedInspectorState =
        inspectorState.transform {
            if (
                it == InspectorState.POPULATED && previousInspectorState == InspectorState.WAITING
            ) {
                delay(400) // Todo: change this
                emit(it)
                delay(500)
                dumpProgress.value = 0f
            } else {
                emit(it)
            }
            previousInspectorState = it
        }

    val showDumpSuggestion = transformedInspectorState.map { it == InspectorState.EMPTY }
    val dumpSuggestionText = "Smash the button."

    val showCurrentDump = transformedInspectorState.map { it == InspectorState.POPULATED }
    val currentDumpInfo =
        selectedDump.map { "${it.nickname} @ ${getFormattedDate(it.timeMilliseconds)}" }

    val showDumpProgress = transformedInspectorState.map { it == InspectorState.WAITING }
    val dumpProgress = MutableStateFlow(0f)
    val dumpProgressText = MutableStateFlow("")

    val isExtractButtonEnabled = transformedInspectorState.map { it != InspectorState.WAITING }

    val areDisplayControlButtonsEnabled =
        transformedInspectorState.map { it == InspectorState.POPULATED }
    val displayCounter =
        combine(displayIndex, displayCount, transformedInspectorState) {
            displayIndex,
            displayCount,
            transformedInspectorState ->
            if (transformedInspectorState == InspectorState.POPULATED) {
                "${displayIndex + 1}/$displayCount"
            } else {
                "?/?"
            }
        }

    val areResizeButtonsEnabled = transformedInspectorState.map { it == InspectorState.POPULATED }

    val isOpenDumpHistoryButtonEnabled =
        transformedInspectorState.map { it == InspectorState.POPULATED }

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
