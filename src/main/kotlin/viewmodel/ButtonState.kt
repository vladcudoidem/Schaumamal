package viewmodel

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import oldModel.InspectorState
import oldModel.notification.Notification
import oldModel.notification.NotificationManager
import kotlin.time.Duration.Companion.milliseconds

class ButtonState(
    inspectorState: StateFlow<InspectorState>,
    private val extract: (onException: (Exception) -> Unit) -> Unit,
    private val notificationManager: NotificationManager
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
        extract {
            val exceptionNotification = Notification(
                description = "Dump failed.",
                timeout = 4000.milliseconds
            )
            notificationManager.notify(exceptionNotification)
        }
    }
}