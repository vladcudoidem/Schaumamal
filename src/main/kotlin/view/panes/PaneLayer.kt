package view.panes

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.awt.Cursor
import shared.Dimensions
import shared.Dimensions.mediumPadding
import view.Spacer
import view.UiLayoutState
import view.panes.properties.SelectedNodeProperties
import view.panes.properties.topbar.LowerPaneTitleBar
import view.panes.topbar.PaneTopBarActionButton
import view.panes.tree.XmlTree
import view.panes.tree.topbar.UpperPaneTopBars
import view.utils.toPx

private val resizingAreaWidth = 10.dp

@Composable
fun PaneLayer(uiLayoutState: UiLayoutState, paneState: PaneState, modifier: Modifier = Modifier) {
    val showXmlTree by paneState.showXmlTree.collectAsState(initial = false)
    val flatXmlTree by paneState.flatXmlTree.collectAsState(initial = emptyList())
    val showSelectedNodeProperties by
        paneState.showSelectedNodeProperties.collectAsState(initial = false)
    val selectedNodePropertyMap by
        paneState.selectedNodePropertyMap.collectAsState(initial = LinkedHashMap())
    val paneWidth by uiLayoutState.paneWidth.collectAsState(0.dp)
    val upperPaneHeight by uiLayoutState.upperPaneHeight.collectAsState(0.dp)

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
            //  Search for comments that contain "break".

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

        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    modifier
                        .fillMaxHeight()
                        .padding(
                            top = Dimensions.mediumPadding,
                            end = Dimensions.mediumPadding,
                            bottom = Dimensions.mediumPadding,
                        ),
            ) {
                // Some arbitrary padding to fit the resizing area properly into the gap between
                // the upper and lower pane.
                Spacer(width = 12.dp)

                ResizingArea(
                    pointerIcon = PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)),
                    onDrag = uiLayoutState::onHorizontalHandleDrag,
                    onDragEnd = uiLayoutState::onHandleDragEnd,
                    modifier = Modifier.fillMaxHeight().width(resizingAreaWidth),
                )

                Column(
                    horizontalAlignment = Alignment.Start,
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

                    ResizingArea(
                        pointerIcon = PointerIcon(Cursor(Cursor.S_RESIZE_CURSOR)),
                        onDrag = uiLayoutState::onVerticalHandleDrag,
                        onDragEnd = uiLayoutState::onHandleDragEnd,
                        modifier = Modifier.fillMaxWidth().height(resizingAreaWidth),
                    )

                    LowerPane(
                        showSelectedNodeProperties = showSelectedNodeProperties,
                        selectedNodePropertyMap = selectedNodePropertyMap,
                        modifier = Modifier.fillMaxHeight().fillMaxWidth(),
                    )
                }
            }

            Column(modifier = Modifier.align(Alignment.CenterStart).fillMaxHeight()) {
                Spacer(
                    height =
                        // This will break easily. Fix later in some way.
                        upperPaneHeight + Dimensions.mediumPadding / 2 + Dimensions.mediumPadding -
                            Dimensions.handleDiameter / 2
                )

                ResizingHandle(
                    onDrag = uiLayoutState::onHandleDrag,
                    onDragEnd = uiLayoutState::onHandleDragEnd,
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
