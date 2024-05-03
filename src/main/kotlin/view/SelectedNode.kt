package view

import AppViewModel
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SelectedNode(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current
    val selectedNode = viewModel.selectedNode

    Text(text = selectedNode.className, color = Colors.secondaryTextColor)
}