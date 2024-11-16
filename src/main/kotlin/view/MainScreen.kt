package view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import shared.Colors.backgroundColor
import view.button.ButtonLayer
import view.notification.NotificationLayer
import view.panes.PaneLayer
import view.screenshot.ScreenshotLayer
import viewmodel.AppViewModel

@Composable
fun MainScreen(
    viewModel: AppViewModel,
    uiLayoutState: UiLayoutState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        ScreenshotLayer(
            uiLayoutState = uiLayoutState,
            screenshotState = viewModel.screenshotState,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        ButtonLayer(
            uiLayoutState = uiLayoutState,
            buttonState = viewModel.buttonState,
            modifier = Modifier.align(Alignment.TopStart)
        )

        PaneLayer(
            uiLayoutState = uiLayoutState,
            paneState = viewModel.paneState,
            modifier = Modifier.align(Alignment.CenterEnd)
        )

        NotificationLayer(
            notificationManager = viewModel.notificationManager
        )
    }
}