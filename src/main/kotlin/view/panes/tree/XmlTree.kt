package view.panes.tree

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import viewmodel.XmlTreeLine
import java.awt.Cursor

@Composable
fun XmlTree(
    flatXmlTree: List<XmlTreeLine>,
    lazyListState: LazyListState,
    horizontalScrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    val verticalScrollbarAdapter = rememberScrollbarAdapter(lazyListState)
    val horizontalScrollbarAdapter = rememberScrollbarAdapter(horizontalScrollState)

    Box(modifier = modifier.fillMaxSize()) {

        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(
                top = mediumPadding,
                bottom = mediumPadding * 4 + scrollbarThickness,
                start = mediumPadding,
                end = mediumPadding * 4 + scrollbarThickness
            ),
            modifier = Modifier
                .horizontalScroll(horizontalScrollState)
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