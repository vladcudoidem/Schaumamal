package view.panes.properties

import AppViewModel
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import viewmodel.CustomScrollbarStyle
import viewmodel.Dimensions.largePadding
import viewmodel.Dimensions.mediumPadding
import viewmodel.Dimensions.scrollbarThickness
import viewmodel.Dimensions.smallPadding
import java.awt.Cursor

@Composable
fun SelectedNodeProperties(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    val verticalScrollbarAdapter = rememberScrollbarAdapter(viewModel.lowerPaneVerticalScrollState)
    val horizontalScrollbarAdapter = rememberScrollbarAdapter(viewModel.lowerPaneHorizontalScrollState)

    Box(modifier = modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .verticalScroll(viewModel.lowerPaneVerticalScrollState)
                .horizontalScroll(viewModel.lowerPaneHorizontalScrollState)
                .padding(mediumPadding)
        ) {
            viewModel.selectedNodePropertyMap.forEach { (property, value) ->
                PropertyRow(property = property, value = value)
            }
        }

        VerticalScrollbar(
            adapter = verticalScrollbarAdapter,
            style = CustomScrollbarStyle,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .padding(
                    top = smallPadding + scrollbarThickness,
                    end = smallPadding,
                    bottom = largePadding
                )
                .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
        )

        HorizontalScrollbar(
            adapter = horizontalScrollbarAdapter,
            style = CustomScrollbarStyle,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(
                    start = largePadding,
                    bottom = smallPadding,
                    end = largePadding
                )
                .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
        )
    }
}