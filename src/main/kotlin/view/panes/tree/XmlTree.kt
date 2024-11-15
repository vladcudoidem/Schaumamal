package view.panes.tree

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import shared.Dimensions.largePadding
import shared.Dimensions.mediumPadding
import shared.Dimensions.scrollbarThickness
import shared.Dimensions.smallPadding
import view.CustomScrollbarStyle
import viewmodel.XmlTreeLine
import viewmodel.toPx
import java.awt.Cursor

@Composable
fun XmlTree(
    flatXmlTree: List<XmlTreeLine>,
    selectedNodeIndex: Int,
    activateScroll: Boolean,
    upperPaneHeight: Dp,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current.density

    val upperPaneLazyListState = rememberLazyListState()
    val upperPaneHorizontalScrollState = rememberScrollState(initial = 0)

    LaunchedEffect(selectedNodeIndex) {
        if (activateScroll) {
            // Scroll to the selected node in the upper right box.
            upperPaneLazyListState.animateScrollToItem(
                index = selectedNodeIndex,
                // Divide the upper pane height by 2 so that the selected node ends up in the center of the Box.
                scrollOffset = - upperPaneHeight.toPx(density).div(2).toInt()
            )
        }
    }

    val verticalScrollbarAdapter = rememberScrollbarAdapter(upperPaneLazyListState)
    val horizontalScrollbarAdapter = rememberScrollbarAdapter(upperPaneHorizontalScrollState)

    Box(modifier = modifier.fillMaxSize()) {

        LazyColumn(
            state = upperPaneLazyListState,
            contentPadding = PaddingValues(
                top = mediumPadding,
                bottom = mediumPadding * 4 + scrollbarThickness,
                start = mediumPadding,
                end = mediumPadding * 4 + scrollbarThickness
            ),
            modifier = Modifier
                .horizontalScroll(upperPaneHorizontalScrollState)
                .animateContentSize()
        ) {
            items(flatXmlTree) { line ->
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