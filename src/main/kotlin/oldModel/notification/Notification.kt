package oldModel.notification

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

data class Notification(
    val description: String,
    val timeout: Duration = 3000.milliseconds
) {
    companion object {
        val Empty = Notification(description = "")
    }
}
