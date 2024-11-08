package view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import org.koin.compose.koinInject
import view.button.ButtonLayer
import view.panes.PaneLayer
import view.screenshot.ScreenshotLayer
import shared.Colors.backgroundColor
import view.notification.NotificationLayer
import viewmodel.AppViewModel

@Composable
fun MainScreen(
    viewModel: AppViewModel = koinInject(),
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current.density

    LaunchedEffect(density) {
        viewModel.onNewDensity(density)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        ScreenshotLayer(modifier = Modifier.align(Alignment.CenterStart))
        ButtonLayer(modifier = Modifier.align(Alignment.TopStart))
        PaneLayer(modifier = Modifier.align(Alignment.CenterEnd))
        NotificationLayer()
    }
}