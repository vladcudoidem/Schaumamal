package view

import AppViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import view.button.ButtonLayer
import view.panes.PaneLayer
import view.screenshot.ScreenshotLayer
import viewmodel.Colors.backgroundColor

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current
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
    }
}