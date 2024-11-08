package view.panes.properties

import androidx.compose.animation.animateContentSize
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
import org.koin.compose.koinInject
import shared.Dimensions.largePadding
import shared.Dimensions.mediumPadding
import shared.Dimensions.scrollbarThickness
import shared.Dimensions.smallPadding
import view.CustomScrollbarStyle
import viewmodel.AppViewModel
import java.awt.Cursor

@Composable
fun SelectedNodeProperties(
    viewModel: AppViewModel = koinInject(),
    modifier: Modifier = Modifier
) {
    val verticalScrollbarAdapter = rememberScrollbarAdapter(viewModel.lowerPaneVerticalScrollState)
    val horizontalScrollbarAdapter = rememberScrollbarAdapter(viewModel.lowerPaneHorizontalScrollState)

    Box(modifier = modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .verticalScroll(viewModel.lowerPaneVerticalScrollState)
                .horizontalScroll(viewModel.lowerPaneHorizontalScrollState)
                .padding(
                    top = mediumPadding,
                    bottom = mediumPadding * 4 + scrollbarThickness,
                    start = mediumPadding,
                    end = mediumPadding * 4 + scrollbarThickness
                )
                .animateContentSize()
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
                    top = largePadding,
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