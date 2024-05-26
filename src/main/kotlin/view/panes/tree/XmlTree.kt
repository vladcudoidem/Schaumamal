package view.panes.tree

import AppViewModel
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import shared.Dimensions.largePadding
import shared.Dimensions.mediumPadding
import shared.Dimensions.scrollbarThickness
import shared.Dimensions.smallPadding
import view.CustomScrollbarStyle
import java.awt.Cursor

@Composable
fun XmlTree(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    val verticalScrollbarAdapter = rememberScrollbarAdapter(viewModel.upperPaneLazyListState)
    val horizontalScrollbarAdapter = rememberScrollbarAdapter(viewModel.upperPaneHorizontalScrollState)

    Box(modifier = modifier.fillMaxSize()) {

        LazyColumn(
            state = viewModel.upperPaneLazyListState,
            contentPadding = PaddingValues(
                top = mediumPadding,
                bottom = mediumPadding + scrollbarThickness,
                start = mediumPadding,
                end = mediumPadding + scrollbarThickness
            ),
            modifier = Modifier
                .horizontalScroll(viewModel.upperPaneHorizontalScrollState)
                .animateContentSize()
        ) {
            items(viewModel.flatXmlTree) { line ->
                XmlTreeLine(line = line)
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