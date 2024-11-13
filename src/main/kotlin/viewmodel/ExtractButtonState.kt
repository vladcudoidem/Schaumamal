package viewmodel

import oldModel.InspectorState
import oldModel.LayoutInspector
import oldModel.notification.Notification
import oldModel.notification.NotificationManager
import kotlin.time.Duration.Companion.milliseconds

class ExtractButtonState(
    private val layoutInspector: LayoutInspector,
    private val notificationManager: NotificationManager
) {
    val isEnabled get() = layoutInspector.state != InspectorState.WAITING
    val text
        get() = when (layoutInspector.state) {
            InspectorState.WAITING -> "Dumping..."
            else -> "Smash the red button to dump."
        }

    fun onButtonPressed() = layoutInspector.extractLayout(
        onException = {
            val exceptionNotification = Notification(
                description = "Dump failed.",
                timeout = 4000.milliseconds
            )
            notificationManager.notify(exceptionNotification)
        }
    )
}