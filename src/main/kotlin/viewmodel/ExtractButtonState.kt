package viewmodel

import oldModel.InspectorState
import oldModel.notification.Notification
import oldModel.notification.NotificationManager
import kotlin.time.Duration.Companion.milliseconds

class ExtractButtonState(
    private val getInspectorState: () -> InspectorState,
    private val extract: (onException: (Exception) -> Unit) -> Unit,
    private val notificationManager: NotificationManager
) {
    val isEnabled get() = getInspectorState() != InspectorState.WAITING
    val text
        get() = when (getInspectorState()) {
            InspectorState.WAITING -> "Dumping..."
            else -> "Smash the red button to dump."
        }

    fun onButtonPressed() = extract {
        val exceptionNotification = Notification(
            description = "Dump failed.",
            timeout = 4000.milliseconds
        )
        notificationManager.notify(exceptionNotification)
    }
}