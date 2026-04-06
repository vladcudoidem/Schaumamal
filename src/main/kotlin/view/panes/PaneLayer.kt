package view.panes

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import shared.Dimensions.mediumPadding
import view.UiLayoutState
import view.panes.properties.SelectedNodeProperties
import view.panes.properties.topbar.LowerPaneTitleBar
import view.panes.topbar.PaneTopBarActionButton
import view.panes.tree.XmlTree
import view.panes.tree.topbar.UpperPaneTopBars
import view.utils.toPx

@Composable
fun PaneLayer(uiLayoutState: UiLayoutState, paneState: PaneState, modifier: Modifier = Modifier) {
    val showXmlTree by paneState.showXmlTree.collectAsState(initial = false)
    val flatXmlTree by paneState.flatXmlTree.collectAsState(initial = emptyList())
    val showSelectedNodeProperties by
        paneState.showSelectedNodeProperties.collectAsState(initial = false)
    val selectedNodePropertyMap by
        paneState.selectedNodePropertyMap.collectAsState(initial = LinkedHashMap())
    val paneWidth by uiLayoutState.paneWidth.collectAsState()
    val upperPaneHeight by uiLayoutState.upperPaneHeight.collectAsState()

    val topBarActions =
        listOf(
            PaneTopBarActionButton(
                iconResource = "icons/locate.svg",
                onClick = { paneState.expandAndScrollToSelectedNode() },
                enabled = paneState.isExpandAndScrollButtonEnabled,
            ),
            PaneTopBarActionButton(
                iconResource = "icons/collapse_all.svg",
                onClick = { paneState.collapseAllLines() },
            ),
            PaneTopBarActionButton(
                iconResource = "icons/expand_all.svg",
                onClick = { paneState.expandAllLines() },
            ),
        )

    val treeListState = rememberLazyListState()

    val density = LocalDensity.current.density
    var treeListViewportHeight by remember { mutableStateOf(0.dp) }

    // Listen for scroll events.
    LaunchedEffect(Unit) {
        paneState.combinedTreeScrollEvents.collect { scrollEvent ->
            val visibleItemsInfo = treeListState.layoutInfo.visibleItemsInfo

            val visibleItemIndexes = visibleItemsInfo.map { it.index }.drop(1).dropLast(1)
            val targetIndex = scrollEvent.targetIndex
            val isIndexValid = targetIndex != -1
            val isScrollNecessary = targetIndex !in visibleItemIndexes

            // Todo: this is not the only place where I am creating loose dependencies for UI stuff.
            //  Create some sort of system for this. It is very easy to change stuff and break it.

            val treeListViewportHeightPx = treeListViewportHeight.toPx(density)
            // We need this to offset the scrolling by a little so the element ends up in the middle
            // of the viewport.
            val selectedNodeHeightPx = visibleItemsInfo.firstOrNull()?.size?.toFloat() ?: 0f
            // Seems like this has to be considered, since list padding values always offset
            // scrolling.
            val topTreeListPaddingPx = mediumPadding.toPx(density)

            val scrollOffset =
                (topTreeListPaddingPx + selectedNodeHeightPx / 2) - treeListViewportHeightPx / 2

            if (isScrollNecessary && isIndexValid) {
                treeListState.animateScrollToItem(
                    index = targetIndex,
                    scrollOffset = scrollOffset.toInt(),
                )
            }
        }
    }

    // As an exception we are not passing the modifier parameter to the outer composable, as we are
    // using the o. c.
    // (the BoxWithConstraints) just for background UI handling and not for any user-facing
    // functionality.
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        LaunchedEffect(maxWidth) { uiLayoutState.onPanesWidthConstraintChanged(maxWidth) }

        LaunchedEffect(maxHeight) { uiLayoutState.onPanesHeightConstraintChanged(maxHeight) }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxHeight().padding(mediumPadding),
        ) {
            Wedge(
                orientation = WedgeOrientation.VERTICAL,
                onDrag = uiLayoutState::onVerticalWedgeDrag,
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(paneWidth),
            ) {
                UpperPane(
                    showXmlTree = showXmlTree,
                    flatXmlTree = flatXmlTree,
                    treeListState = treeListState,
                    topBarActions = topBarActions,
                    onTreeListViewportHeightChanged = { treeListViewportHeight = it },
                    modifier = Modifier.height(upperPaneHeight).fillMaxWidth(),
                )

                Wedge(
                    orientation = WedgeOrientation.HORIZONTAL,
                    onDrag = uiLayoutState::onHorizontalWedgeDrag,
                )

                LowerPane(
                    showSelectedNodeProperties = showSelectedNodeProperties,
                    selectedNodePropertyMap = selectedNodePropertyMap,
                    modifier = Modifier.fillMaxHeight().fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun UpperPane(
    showXmlTree: Boolean,
    flatXmlTree: List<XmlTreeLine>,
    treeListState: LazyListState,
    topBarActions: List<PaneTopBarActionButton>,
    onTreeListViewportHeightChanged: (Dp) -> Unit,
    modifier: Modifier = Modifier,
) {
    PaneContainer(showContent = showXmlTree, placeholder = "Perform a dump.", modifier = modifier) {
        Column {
            UpperPaneTopBars(
                topBarActions = topBarActions,
                onSearch = {},
                onSearchNext = {},
                onSearchPrevious = {},
                currentSearchResultIndex = 0,
                totalSearchResultCount = 0,
            )

            XmlTree(
                flatXmlTree = flatXmlTree,
                treeListState = treeListState,
                onTreeListViewportHeightChanged = onTreeListViewportHeightChanged,
            )
        }
    }
}

@Composable
private fun LowerPane(
    showSelectedNodeProperties: Boolean,
    selectedNodePropertyMap: LinkedHashMap<String, String>,
    modifier: Modifier = Modifier,
) {
    PaneContainer(
        showContent = showSelectedNodeProperties,
        placeholder = "No node selected.",
        modifier = modifier,
    ) {
        Column {
            LowerPaneTitleBar()
            SelectedNodeProperties(selectedNodePropertyMap)
        }
    }
}
