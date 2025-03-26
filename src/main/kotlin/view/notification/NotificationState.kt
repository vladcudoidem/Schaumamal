package view.notification

import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import viewmodel.notification.Notification

class NotificationState(
    private val notifications: SharedFlow<Notification>,
    scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
) {
    private val _active = MutableStateFlow(false)
    val active = _active.asStateFlow()

    private val _latestNotification = MutableStateFlow(Notification.Empty)
    val latestNotification = _latestNotification.asStateFlow()

    private val minTimeoutBetweenNotifications = 500.milliseconds

    init {
        scope.launch {
            notifications.collect {
                _active.value = true
                _latestNotification.value = it
                delay(it.timeout)

                _active.value = false
                delay(minTimeoutBetweenNotifications)
            }
        }
    }
}
