package view.notification

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import shared.Dimensions

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotificationLayer(notificationState: NotificationState, modifier: Modifier = Modifier) {
    val activeNotifications by
        notificationState.activeNotifications.collectAsState(initial = emptyList())
    val verticalScrollState = rememberLazyListState()

    // Todo: create toasts for quick feedback
    // Todo: use tool tips for quick info and quick feedback

    // Todo: make the list scroll down when a new notification is added
    // Todo: add in and out animations for notifications
    // Todo: experiment with listing the non-persistent always at the bottom

    Box(contentAlignment = Alignment.BottomCenter, modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = verticalScrollState,
            verticalArrangement = Arrangement.spacedBy(Dimensions.smallPadding),
            contentPadding =
                PaddingValues(top = Dimensions.largePadding, bottom = Dimensions.largePadding),
            reverseLayout = true,
        ) {
            items(
                activeNotifications,
                key = {
                    // Todo: there can be conflicts for same data class info. Use UUID.
                    it.hashCode()
                },
            ) {
                NotificationPill(
                    activeNotification = it,
                    hideNotification = { notificationState.hideNotification(it) },
                )
            }
        }
    }
}
