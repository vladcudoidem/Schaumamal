package view

import AppViewModel
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ToolbarButton(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    Button(
        modifier = modifier.size(50.dp),
        shape = RoundedCornerShape(5.dp),
        onClick = {
            viewModel.extractLayout()
        }
    ) {
        Text("D")
    }
}