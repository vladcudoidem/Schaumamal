package view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun MainScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        ScreenshotBox(modifier = Modifier.fillMaxSize())
        RedButton(modifier = Modifier.align(Alignment.TopStart))
        FloatingPanes(modifier = Modifier.align(Alignment.TopEnd))
    }
}