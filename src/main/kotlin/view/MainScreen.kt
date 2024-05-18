package view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import view.Colors.backgroundColor
import view.button.ButtonLayer
import view.panes.PaneLayer
import view.screenshot.ScreenshotLayer

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        ScreenshotLayer(modifier = Modifier.fillMaxSize())
        ButtonLayer(modifier = Modifier.align(Alignment.TopStart))
        PaneLayer(modifier = Modifier.align(Alignment.CenterEnd))
    }
}