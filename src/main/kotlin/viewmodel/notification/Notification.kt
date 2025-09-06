package viewmodel.notification

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

data class Notification(
    val title: String,
    val description: String,
    val severity: NotificationSeverity,
    val timestamp: Long = System.currentTimeMillis(),
    val exitStrategy: NotificationExitStrategy = NotificationExitStrategy.Manual,
    val actions: List<NotificationAction> = listOf(),
) {
    companion object {
        val Empty = Notification(title = "", description = "", severity = NotificationSeverity.INFO)

        // Todo: remove this
        fun getRandomTestNotification(): Notification {
            val isManualExit = (1..100).random().let { it > 50 }

            return Notification(
                title = "Dump problem",
                description =
                    "The dump could not be retrieved from the device. The app will attempt to shut down. It will be a bigger problem in the future. Try to debug and send logs.",
                exitStrategy =
                    if (!isManualExit) NotificationExitStrategy.Timeout(5000.milliseconds)
                    else NotificationExitStrategy.Manual,
                severity = NotificationSeverity.entries.random(),
                actions =
                    listOf(
                        NotificationAction(title = "Fake action", block = {}),
                        NotificationAction(
                            title = "Another fake action, this does more",
                            block = {},
                        ),
                    ),
            )
        }
    }
}

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
