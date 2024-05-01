package view

import AppViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RoundButton(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    Box(modifier = modifier.padding(10.dp)) {
        Button(
            onClick = { viewModel.extractLayout() },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(backgroundColor = Colors.buttonColor),
            modifier = Modifier.size(40.dp)
        ) { }
    }
}