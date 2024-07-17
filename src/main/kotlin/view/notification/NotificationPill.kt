package view.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import oldModel.notification.Notification
import shared.Colors.accentColor
import shared.Colors.primaryTextColor
import shared.Dimensions.largePadding
import shared.Dimensions.notificationPillHeight

@Composable
fun NotificationPill(
    notification: Notification,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(accentColor)
            .padding(start = largePadding, end = largePadding)
            .height(notificationPillHeight)
    ) {
        Text(
            text = notification.description,
            color = primaryTextColor,
            fontFamily = FontFamily.SansSerif,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}