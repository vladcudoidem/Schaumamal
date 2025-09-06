package view.notification

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import viewmodel.notification.Notification
import viewmodel.notification.NotificationExitStrategy

private typealias CancellableActiveNotification = Pair<ActiveNotification, Job?>

class NotificationState(
    private val notifications: SharedFlow<Notification>,
    scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
) {
    private val activeNotificationsAndTimeoutJobs:
        MutableStateFlow<List<CancellableActiveNotification>> =
        MutableStateFlow(emptyList())
    val activeNotifications =
        activeNotificationsAndTimeoutJobs.map {
            it.map { (activeNotification, job) -> activeNotification }
        }

    init {
        scope.launch {
            notifications.collect { newNotification ->
                // Do not add notification if the timeout is zero.
                if (
                    newNotification.exitStrategy is NotificationExitStrategy.Timeout &&
                        newNotification.exitStrategy.value.inWholeMilliseconds == 0L
                ) {
                    return@collect
                }

                val activeNotification = ActiveNotification(newNotification)

                val exitStrategy = newNotification.exitStrategy
                val notificationTimeoutJob =
                    if (exitStrategy is NotificationExitStrategy.Timeout) {
                        scope.launch {
                            hideNotificationAfterTimeout(
                                activeNotification,
                                timeoutMs = exitStrategy.value.inWholeMilliseconds,
                            )
                        }
                    } else {
                        null
                    }

                activeNotificationsAndTimeoutJobs.update {
                    listOf(activeNotification to notificationTimeoutJob) + it
                }
            }
        }
    }

    private suspend fun hideNotificationAfterTimeout(
        activeNotification: ActiveNotification,
        timeoutMs: Long,
    ) {
        // Behavior not defined for zero timeout.
        if (timeoutMs == 0L) return

        val stepMs = 16L // is about 60fps
        // We increment the elapsed time and compare with the timeout.
        var effectiveElapsedTimeMs = 0L
        // It makes sense to initialize this with the current time at start.
        var lastLoopTimeMs = getTime()

        while (true) {
            delay(stepMs)

            // Calculate increment and update current loop time for next increment.
            val currentTimeMs = getTime()
            val timeSinceLastLoopMs = currentTimeMs - lastLoopTimeMs
            lastLoopTimeMs = currentTimeMs

            // Update progress if timeout is not paused.
            if (!activeNotification.isPaused) {
                effectiveElapsedTimeMs += timeSinceLastLoopMs

                updateNotificationLeftoverProgress(
                    activeNotification,
                    elapsedMs = effectiveElapsedTimeMs,
                    timeoutMs,
                )
            }

            // Exit loop if timeout has expired.
            val shouldShowNotification = effectiveElapsedTimeMs < timeoutMs
            if (!shouldShowNotification) break
        }

        hideNotification(activeNotification)
    }

    private fun updateNotificationLeftoverProgress(
        activeNotification: ActiveNotification,
        elapsedMs: Long,
        timeoutMs: Long,
    ) {
        val progress = elapsedMs.toFloat() / timeoutMs
        val newLeftoverProgress = (1f - progress).coerceIn(0f, 1f)
        activeNotification.leftoverProgress.update { newLeftoverProgress }
    }

    private fun getTime() = System.currentTimeMillis()

    fun hideNotification(activeNotification: ActiveNotification) {
        activeNotificationsAndTimeoutJobs.update { it.removeFirst(element = activeNotification) }
    }

    private fun List<CancellableActiveNotification>.removeFirst(
        element: ActiveNotification
    ): List<CancellableActiveNotification> {
        val activeNotifications = map { it.first }
        val indexToRemove = activeNotifications.indexOfFirst { it === element }
        if (indexToRemove == -1) return this

        val timeoutToCancel = getOrNull(indexToRemove)?.second
        timeoutToCancel?.cancel()

        val updatedNotifications = toMutableList().apply { removeAt(indexToRemove) }
        return updatedNotifications
    }
}
