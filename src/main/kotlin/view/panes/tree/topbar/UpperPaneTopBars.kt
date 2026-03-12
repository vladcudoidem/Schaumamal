package view.panes.tree.topbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import view.panes.topbar.PaneTopBarActionButton

@Composable
fun UpperPaneTopBars(
    topBarActions: List<PaneTopBarActionButton>,
    onSearchNext: () -> Unit,
    onSearchPrevious: () -> Unit,
    searchResultText: String,
    query: String,
    onQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    toggleSearchActive: () -> Unit,
    isImageSearchActive: Boolean,
    toggleImageSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        UpperPaneTitleBar(
            topBarActions = topBarActions,
            isSearchModeActive = isSearchActive,
            onSearchClick = toggleSearchActive,
        )

        AnimatedVisibility(
            visible = isSearchActive,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            SearchBar(
                query = query,
                onQueryChange = onQueryChange,
                onNext = onSearchNext,
                onPrevious = onSearchPrevious,
                searchResultText = searchResultText,
                isImageSearchActive = isImageSearchActive,
                toggleImageSearch = toggleImageSearch,
            )
        }
    }
}
