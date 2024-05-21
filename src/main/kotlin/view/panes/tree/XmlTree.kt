package view.panes.tree

import AppViewModel
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import viewmodel.Dimensions.mediumPadding

@Composable
fun XmlTree(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    LazyColumn(
        state = viewModel.upperPaneLazyListState,
        contentPadding = PaddingValues(mediumPadding),
        modifier = modifier
            .horizontalScroll(viewModel.upperPaneHorizontalScrollState)
            .animateContentSize()
    ) {
        items(viewModel.flatXmlTree) { line ->
            XmlTreeLine(line = line)
        }
    }
}