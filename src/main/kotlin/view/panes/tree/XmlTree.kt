package view.panes.tree

import AppViewModel
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import view.Dimensions.mediumPadding

@Composable
fun XmlTree(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    Column( // TODO use LazyColumn?
        modifier = modifier
            .verticalScroll(viewModel.upperPaneVerticalScrollState)
            .horizontalScroll(viewModel.upperPaneHorizontalScrollState)
    ) {
        Spacer(modifier = Modifier.height(mediumPadding))

        viewModel.flatXmlTree.forEach { line ->
            XmlTreeLine(line = line)
        }

        Spacer(modifier = Modifier.height(mediumPadding))
    }
}