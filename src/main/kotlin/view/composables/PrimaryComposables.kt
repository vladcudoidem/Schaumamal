package view.composables

import AppViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.dp
import model.Constants.LOCAL_SCREENSHOT_PATH
import model.LayoutPrinter
import java.io.File
import java.io.FileInputStream

@Composable
fun Toolbar(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ToolbarButton()
    }
}

@Composable
fun ScreenshotBox(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(5.dp))
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        if (viewModel.isInspectorPopulated) {
            Image(
                bitmap = viewModel.layoutData.screenshotBitmap,
                contentDescription = null
            )
        } else {
            Text("No data...")
        }
    }
}

@Composable
fun UiTreeBox(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(5.dp))
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        if (viewModel.isInspectorPopulated){
            val scrollState = rememberScrollState()

            Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
                LayoutPrinter.getStructure(viewModel.layoutData.rootNode)
            }
        } else {
            Text("No data...")
        }
    }
}