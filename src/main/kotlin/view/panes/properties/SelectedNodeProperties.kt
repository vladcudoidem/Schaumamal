package view.panes.properties

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
fun SelectedNodeProperties(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    Column(
        modifier = modifier
            .verticalScroll(viewModel.lowerPaneVerticalScrollState)
            .horizontalScroll(viewModel.lowerPaneHorizontalScrollState)
    ) {
        Spacer(modifier = Modifier.height(mediumPadding))

        viewModel.selectedNodePropertyMap.forEach { (property, value) ->
            PropertyRow(property = property, value = value)
        }

        Spacer(modifier = Modifier.height(mediumPadding))
    }
}