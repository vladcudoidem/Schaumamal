package view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import shared.Colors.backgroundColor
import view.button.ButtonLayer
import view.button.ButtonState
import view.floatingWindow.FloatingWindowLayer
import view.floatingWindow.FloatingWindowState
import view.notification.NotificationLayer
import view.notification.NotificationState
import view.panes.PaneLayer
import view.panes.PaneState
import view.screenshot.ScreenshotLayer
import view.screenshot.ScreenshotState

@Composable
fun MainScreen(
    screenshotState: ScreenshotState,
    buttonState: ButtonState,
    paneState: PaneState,
    uiLayoutState: UiLayoutState,
    floatingWindowState: FloatingWindowState,
    notificationState: NotificationState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize().background(backgroundColor)) {
        ScreenshotLayer(
            uiLayoutState = uiLayoutState,
            screenshotState = screenshotState,
            modifier = Modifier.align(Alignment.CenterStart),
        )

        ButtonLayer(
            uiLayoutState = uiLayoutState,
            buttonState = buttonState,
            modifier = Modifier.align(Alignment.TopStart),
        )

        PaneLayer(
            uiLayoutState = uiLayoutState,
            paneState = paneState,
            modifier = Modifier.align(Alignment.CenterEnd),
        )

        FloatingWindowLayer(floatingWindowState = floatingWindowState)

        NotificationLayer(notificationState = notificationState)
    }
}
