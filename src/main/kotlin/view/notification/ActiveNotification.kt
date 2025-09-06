package view.notification

import kotlinx.coroutines.flow.MutableStateFlow
import viewmodel.notification.Notification

data class ActiveNotification(val notification: Notification) {
    val leftoverProgress: MutableStateFlow<Float> = MutableStateFlow(1f)
    var isPaused: Boolean = false
}
