package view.notification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import oldModel.notification.Notification
import shared.Dimensions.largePadding

@Composable
fun NotificationLayer(
    showNotification: Boolean,
    currentNotification: Notification,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier
            .fillMaxSize()
            .padding(largePadding)
    ) {
        AnimatedVisibility(
            visible = showNotification,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight * 2 }
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight * 2 }
            )
        ) {
            NotificationPill(currentNotification)
        }
    }
}