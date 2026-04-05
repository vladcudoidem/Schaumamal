package view.panes

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.v2.ScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import java.awt.Cursor
import shared.Dimensions.largePadding
import shared.Dimensions.smallPadding

@Composable
fun ScrollableBox(
    verticalScrollbarAdapter: ScrollbarAdapter,
    horizontalScrollbarAdapter: ScrollbarAdapter,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        content()

        VerticalScrollbar(
            adapter = verticalScrollbarAdapter,
            style = CustomScrollbarStyle,
            modifier =
                Modifier.align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .padding(top = smallPadding, end = smallPadding, bottom = largePadding)
                    .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR))),
        )

        HorizontalScrollbar(
            adapter = horizontalScrollbarAdapter,
            style = CustomScrollbarStyle,
            modifier =
                Modifier.align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(start = largePadding, bottom = smallPadding, end = largePadding)
                    .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR))),
        )
    }
}
