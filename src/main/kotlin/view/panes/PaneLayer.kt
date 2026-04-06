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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
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

    // Listen for scroll events.
    LaunchedEffect(Unit) {
        paneState.combinedTreeScrollEvents.collect { scrollEvent ->
            val visibleItemsInfo = treeListState.layoutInfo.visibleItemsInfo

            val visibleItemIndexes = visibleItemsInfo.map { it.index }.drop(1).dropLast(1)
            val targetIndex = scrollEvent.targetIndex
            val isIndexValid = targetIndex != -1
            val isScrollNecessary = targetIndex !in visibleItemIndexes

            val selectedNodeHeightPx = visibleItemsInfo.firstOrNull()?.size ?: 0
            // Divide the upper pane height by 2 so that the selected node ends up in the center
            // of the Box.
            val scrollOffset =
                -upperPaneHeight.toPx(density).div(2).minus(selectedNodeHeightPx).toInt()

            if (isScrollNecessary && isIndexValid) {
                treeListState.animateScrollToItem(index = targetIndex, scrollOffset = scrollOffset)
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

            XmlTree(flatXmlTree = flatXmlTree, treeListState = treeListState)
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
