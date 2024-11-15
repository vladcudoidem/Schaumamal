package viewmodel

import oldModel.InspectorState
import oldModel.notification.Notification
import oldModel.notification.NotificationManager
import kotlin.time.Duration.Companion.milliseconds

class ButtonState(
    private val getInspectorState: () -> InspectorState,
    private val extract: (onException: (Exception) -> Unit) -> Unit,
    private val notificationManager: NotificationManager
) {
    val areResizeButtonsEnabled get() = getInspectorState() == InspectorState.POPULATED

    val isExtractButtonEnabled get() = getInspectorState() != InspectorState.WAITING
    val extractButtonText
        get() = when (getInspectorState()) {
            InspectorState.WAITING -> "Dumping..."
            else -> "Smash the red button to dump."
        }

    fun onExtractButtonPressed() = extract {
        val exceptionNotification = Notification(
            description = "Dump failed.",
            timeout = 4000.milliseconds
        )
        notificationManager.notify(exceptionNotification)
    }
}