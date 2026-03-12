package view.panes.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import shared.Colors.elevatedBackgroundColor
import shared.Colors.paneBorderColor
import shared.Dimensions.defaultTopBarContentHeight
import shared.Dimensions.paneBorderWidth

@Composable
fun TopBarContainer(
    modifier: Modifier = Modifier,
    isFirstTopBar: Boolean = false,
    contentHeight: Dp = defaultTopBarContentHeight,
    content: @Composable () -> Unit,
) {
    val height: Dp
    val alignment: Alignment

    if (isFirstTopBar) {
        // We use 1 dp more for the first top bar because the 1 dp wide border of the pane covers
        // the top.
        height = contentHeight + 2.dp
        alignment = Alignment.CenterStart
    } else {
        height = contentHeight + 1.dp
        alignment = Alignment.TopStart
    }

    Box(
        contentAlignment = alignment,
        modifier =
            modifier.fillMaxWidth().height(height).background(elevatedBackgroundColor).drawBehind {
                val lineWidthPx = paneBorderWidth.toPx()
                drawLine(
                    color = paneBorderColor,
                    start = Offset(lineWidthPx, size.height - lineWidthPx / 2),
                    end = Offset(size.width - lineWidthPx, size.height - lineWidthPx / 2),
                    strokeWidth = lineWidthPx,
                )
            },
    ) {
        Box(modifier = Modifier.height(contentHeight)) { content() }
    }
}
