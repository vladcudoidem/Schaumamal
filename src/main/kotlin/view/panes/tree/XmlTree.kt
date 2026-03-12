package view.panes.tree

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import shared.Dimensions.mediumPadding
import shared.Dimensions.scrollbarThickness
import view.panes.ScrollableBox
import view.panes.XmlTreeLine

@Composable
fun XmlTree(
    flatXmlTree: List<XmlTreeLine>,
    treeListState: LazyListState,
    onTreeListViewportHeightChanged: (Dp) -> Unit,
    modifier: Modifier = Modifier,
) {
    val horizontalScrollState = rememberScrollState(initial = 0)

    val visibleTreeLines =
        flatXmlTree.filter {
            val isVisible: Boolean by it.isVisible.collectAsState(true)
            isVisible
        }

    BoxWithConstraints {
        LaunchedEffect(maxHeight) { onTreeListViewportHeightChanged(maxHeight) }

        ScrollableBox(
            verticalScrollbarAdapter = rememberScrollbarAdapter(treeListState),
            horizontalScrollbarAdapter = rememberScrollbarAdapter(horizontalScrollState),
            modifier = modifier,
        ) {
            LazyColumn(
                state = treeListState,
                contentPadding =
                    PaddingValues(
                        top = mediumPadding,
                        bottom = mediumPadding * 4 + scrollbarThickness,
                        start = mediumPadding,
                        end = mediumPadding * 4 + scrollbarThickness,
                    ),
                modifier =
                    Modifier.fillMaxSize()
                        .horizontalScroll(horizontalScrollState)
                        .animateContentSize(),
            ) {
                items(visibleTreeLines) { line -> XmlTreeLine(line = line) }
            }
        }
    }
}
