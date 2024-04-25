package view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ToolbarButton(modifier: Modifier = Modifier) {
    Button(
        modifier = modifier
            .size(50.dp),
        shape = RoundedCornerShape(5.dp),
        onClick = { }
    ) {
        Text("D")
    }
}