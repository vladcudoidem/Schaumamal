package view.panes.properties

import AppViewModel
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import viewmodel.Dimensions.mediumPadding

@Composable
fun SelectedNodeProperties(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    Column(
        modifier = modifier
            .verticalScroll(viewModel.lowerPaneVerticalScrollState)
            .horizontalScroll(viewModel.lowerPaneHorizontalScrollState)
            .padding(mediumPadding)
    ) {
        viewModel.selectedNodePropertyMap.forEach { (property, value) ->
            PropertyRow(property = property, value = value)
        }
    }
}