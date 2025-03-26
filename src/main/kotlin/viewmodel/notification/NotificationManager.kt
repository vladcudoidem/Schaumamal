package viewmodel.notification

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext

class NotificationManager {

    private val _notifications =
        MutableSharedFlow<Notification>(
            extraBufferCapacity = 10, // Todo: raise this after testing.
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
    val notifications = _notifications.asSharedFlow()

    suspend fun notify(notification: Notification) {
        withContext(Dispatchers.Default) { _notifications.emit(notification) }
    }
}
