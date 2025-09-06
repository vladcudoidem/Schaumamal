package view.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.awt.Cursor
import shared.Colors
import shared.Colors.elevatedBackgroundColor
import shared.Colors.paneBorderColor
import shared.Dimensions
import shared.Dimensions.largeCornerRadius
import shared.Dimensions.mediumPadding
import shared.Dimensions.paneBorderWidth
import shared.Dimensions.smallCornerRadius
import shared.Dimensions.smallPadding
import view.button.RoundIconButton
import view.utils.getFormattedDate
import viewmodel.notification.NotificationAction
import viewmodel.notification.NotificationExitStrategy
import viewmodel.notification.NotificationSeverity

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NotificationPill(
    activeNotification: ActiveNotification,
    hideNotification: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val notification = activeNotification.notification
    val leftoverProgress by activeNotification.leftoverProgress.collectAsState()

    Box(
        modifier =
            modifier
                .width(Dimensions.notificationWidth)
                .onPointerEvent(PointerEventType.Enter) { activeNotification.isPaused = true }
                .onPointerEvent(PointerEventType.Exit) { activeNotification.isPaused = false }
    ) {
        Box(
            modifier =
                Modifier.padding(top = mediumPadding, end = mediumPadding)
                    .shadow(elevation = 5.dp, shape = RoundedCornerShape(largeCornerRadius))
                    .pointerInput(Unit) { /* Only consume the click. */ }
                    .clip(RoundedCornerShape(largeCornerRadius))
                    .background(elevatedBackgroundColor)
                    .border(
                        width = paneBorderWidth,
                        color = paneBorderColor,
                        shape = RoundedCornerShape(largeCornerRadius),
                    )
        ) {
            LinearProgressIndicator(
                progress = leftoverProgress,
                color = Color.White.copy(alpha = 0.03f),
                backgroundColor = Color.Transparent,
                strokeCap = StrokeCap.Butt,
                modifier = Modifier.matchParentSize(),
            )

            Column(
                modifier = Modifier.padding(mediumPadding + smallPadding),
                verticalArrangement = Arrangement.spacedBy(mediumPadding),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(smallPadding),
                ) {
                    NotificationIcon(notification.severity)
                    Text(
                        text = notification.title,
                        fontWeight = FontWeight.ExtraBold,
                        color = Colors.primaryTextColor,
                    )
                    Text(
                        text = "@ ${getFormattedDate(notification.timestamp)}",
                        color = Colors.discreteTextColor,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (notification.exitStrategy is NotificationExitStrategy.Manual) {
                        Text(text = "(persistent notification)", color = Colors.discreteTextColor)
                    }
                }
                SelectionContainer {
                    Text(text = notification.description, color = Colors.primaryTextColor)
                }
                NotificationActionList(notification.actions, hideNotification)
            }
        }

        RoundIconButton(
            onClick = { hideNotification() },
            iconPainter = painterResource("icons/close.svg"),
            buttonModifier =
                Modifier.shadow(elevation = 5.dp, shape = CircleShape)
                    .align(Alignment.TopEnd)
                    .border(width = paneBorderWidth, color = paneBorderColor, shape = CircleShape)
                    .size(25.dp),
        )
    }
}

@Composable
fun NotificationIcon(severity: NotificationSeverity, modifier: Modifier = Modifier) {
    val resourcePath: String
    val color: Color
    when (severity) {
        NotificationSeverity.INFO -> {
            resourcePath = "icons/info.svg"
            color = Colors.infoIconColor
        }
        NotificationSeverity.WARNING -> {
            resourcePath = "icons/warning.svg"
            color = Colors.warningIconColor
        }
        NotificationSeverity.ERROR -> {
            resourcePath = "icons/error.svg"
            color = Colors.errorIconColor
        }
    }

    Icon(
        painter = painterResource(resourcePath),
        contentDescription = null,
        tint = color,
        modifier = modifier.size(25.dp),
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NotificationActionList(
    actions: List<NotificationAction>,
    hideNotification: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        actions.forEach {
            var backgroundColor by remember { mutableStateOf(Color.Transparent) }
            var textColor by remember { mutableStateOf(Colors.notificationActionTextColor) }

            Text(
                text = it.title,
                color = textColor,
                modifier =
                    Modifier.clip(RoundedCornerShape(smallCornerRadius))
                        .background(backgroundColor)
                        .clickable {
                            it.block()
                            if (it.shouldHideNotification) {
                                hideNotification()
                            }
                        }
                        .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
                        .onPointerEvent(PointerEventType.Enter) {
                            backgroundColor = Colors.notificationActionTextColor
                            textColor = elevatedBackgroundColor
                        }
                        .onPointerEvent(PointerEventType.Exit) {
                            backgroundColor = Color.Transparent
                            textColor = Colors.notificationActionTextColor
                        }
                        .padding(smallPadding),
            )
        }
    }
}
