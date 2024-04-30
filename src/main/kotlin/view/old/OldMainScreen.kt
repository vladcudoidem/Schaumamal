package view.old

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OldMainScreen() {
    Row(
        modifier = Modifier.padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Toolbar()
        ScreenshotBox(modifier = Modifier.weight(0.5f))
        UiTreeBox(modifier = Modifier.weight(0.5f))
    }
}