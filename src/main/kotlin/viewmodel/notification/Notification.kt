package viewmodel.notification

import kotlin.time.Duration

data class Notification(
    val title: String,
    val description: String,
    val severity: NotificationSeverity,
    val timestamp: Long = System.currentTimeMillis(),
    val exitStrategy: NotificationExitStrategy = NotificationExitStrategy.Manual,
    val actions: List<NotificationAction> = listOf(),
)

sealed interface NotificationExitStrategy {
    data class Timeout(val value: Duration) : NotificationExitStrategy

    data object Manual : NotificationExitStrategy
}

fun timeout(value: Duration) = NotificationExitStrategy.Timeout(value)

data class NotificationAction(
    val title: String,
    val block: () -> Unit,
    /** Whether the notification should be hidden when the action button is clicked. */
    val shouldHideNotification: Boolean = false,
)

enum class NotificationSeverity {
    INFO,
    WARNING,
    ERROR,
}
