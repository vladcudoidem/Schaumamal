package view.composables

import LocalViewModel
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import model.Utils

@Composable
fun ToolbarButton(modifier: Modifier = Modifier) {
    val viewModel = LocalViewModel.current

    Button(
        modifier = modifier
            .size(50.dp),
        shape = RoundedCornerShape(5.dp),
        onClick = {
            viewModel.dumpXml()
        }
    ) {
        Text("D")
    }
}