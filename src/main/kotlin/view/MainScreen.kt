package view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import oldModel.notification.NotificationManager
import shared.Colors.backgroundColor
import view.button.ButtonLayer
import view.notification.NotificationLayer
import view.panes.PaneLayer
import view.screenshot.ScreenshotLayer
import viewmodel.ButtonState
import viewmodel.PaneState
import viewmodel.ScreenshotState

@Composable
fun MainScreen(
    screenshotState: ScreenshotState,
    buttonState: ButtonState,
    paneState: PaneState,
    uiLayoutState: UiLayoutState,
    notificationManager: NotificationManager,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        ScreenshotLayer(
            uiLayoutState = uiLayoutState,
            screenshotState = screenshotState,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        ButtonLayer(
            uiLayoutState = uiLayoutState,
            buttonState = buttonState,
            modifier = Modifier.align(Alignment.TopStart)
        )

        PaneLayer(
            uiLayoutState = uiLayoutState,
            paneState = paneState,
            modifier = Modifier.align(Alignment.CenterEnd)
        )

        NotificationLayer(
            notificationManager = notificationManager
        )
    }
}