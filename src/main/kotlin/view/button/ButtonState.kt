package view.button

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import model.InspectorState

class ButtonState(
    inspectorState: StateFlow<InspectorState>,
    private val extract: () -> Unit
) {

    val areResizeButtonsEnabled = inspectorState.map { it == InspectorState.POPULATED }

    val isExtractButtonEnabled = inspectorState.map { it != InspectorState.WAITING }
    val extractButtonText = inspectorState.map {
        when (it) {
            InspectorState.WAITING -> "Dumping..."
            else -> "Smash the red button to dump."
        }
    }

    fun onExtractButtonPressed() {
        extract()
    }
}