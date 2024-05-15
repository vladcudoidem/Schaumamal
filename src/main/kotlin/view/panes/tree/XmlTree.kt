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

// TODO do this with LazyColumn. Is that possible?

@Composable
fun XmlTree(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    Column(
        modifier = modifier
            .verticalScroll(viewModel.upperPaneVerticalScrollState)
            .horizontalScroll(viewModel.upperPaneHorizontalScrollState)
    ) {
        Spacer(modifier = Modifier.height(mediumPadding))

        viewModel.flatXmlTree.forEach {
            XmlTreeLine(
                text = it.text,
                textBackgroundColor = it.textBackgroundColor,
                depth = it.depth,
                onClickText = it.onClickText,
                onTreeLineGloballyPositioned = it.onTreeLineGloballyPositioned
            )
        }

        Spacer(modifier = Modifier.height(mediumPadding))
    }
}