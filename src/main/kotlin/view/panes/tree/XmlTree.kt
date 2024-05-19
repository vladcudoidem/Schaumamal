package view.panes.tree

import AppViewModel
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import viewmodel.Dimensions.mediumPadding

@Composable
fun XmlTree(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    // TODO implement this with LazyColumn.
    Column(
        modifier = modifier
            .verticalScroll(viewModel.upperPaneVerticalScrollState)
            .horizontalScroll(viewModel.upperPaneHorizontalScrollState)
            .padding(mediumPadding)
    ) {
        viewModel.flatXmlTree.forEach { line ->
            XmlTreeLine(line = line)
        }
    }
}