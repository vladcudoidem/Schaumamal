package oldModel.notification

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class NotificationManager {
    var active by mutableStateOf(false)
    var latestNotification by mutableStateOf(Notification.Empty)

    private var dispatching = false
    private var queue = mutableListOf<Notification>()

    private val timeoutBetweenNotifications = 500.milliseconds

    // Todo: inject and clean up
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    fun notify(notification: Notification) {
        queue.add(notification)

        if (!dispatching) {
            dispatching = true
            coroutineScope.launch { dispatch() }
        }
    }

    private suspend fun dispatch() {
        while (queue.isNotEmpty()) {
            latestNotification = queue.removeFirst()
            active = true
            delay(latestNotification.timeout)

            active = false
            delay(timeoutBetweenNotifications)
        }

        dispatching = false
    }
}