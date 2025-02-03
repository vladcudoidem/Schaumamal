package view.button

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import model.InspectorState
import viewmodel.Direction

class ButtonState(
    inspectorState: StateFlow<InspectorState>,
    displayIndex: StateFlow<Int>,
    displayCount: StateFlow<Int>,
    private val extract: () -> Unit,
    private val switchDisplay: (Direction) -> Unit
) {
    val areResizeButtonsEnabled = inspectorState.map { it == InspectorState.POPULATED }

    val isExtractButtonEnabled = inspectorState.map { it != InspectorState.WAITING }
    val extractButtonText = inspectorState.map {
        when (it) {
            InspectorState.WAITING -> "Dumping..."
            else -> "Smash the red button to dump."
        }
    }

    val areDisplayControlButtonsEnabled = inspectorState.map { it == InspectorState.POPULATED }
    val displayCounter =
        combine(displayIndex, displayCount, inspectorState) { displayIndex, displayCount, inspectorState ->
            if (inspectorState == InspectorState.POPULATED) {
                "${displayIndex + 1}/$displayCount"
            } else {
                "?/?"
            }
        }

    fun onExtractButtonPressed() {
        extract()
    }

    fun onNextDisplayButtonPressed() {
        switchDisplay(Direction.NEXT)
    }

    fun onPreviousDisplayButtonPressed() {
        switchDisplay(Direction.PREVIOUS)
    }
}