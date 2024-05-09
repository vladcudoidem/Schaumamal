package view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import view.button.RoundButton
import view.panels.FloatingPanes
import view.screenshot.Screenshot

@Composable
fun MainScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.backgroundColor)
    ) {
        Screenshot(modifier = Modifier.fillMaxSize())
        RoundButton(modifier = Modifier.align(Alignment.TopStart))
        FloatingPanes(modifier = Modifier.align(Alignment.TopEnd))
    }
}