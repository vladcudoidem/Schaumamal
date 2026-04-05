package view.panes.tree.topbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import view.panes.topbar.PaneTopBarActionButton

@Composable
fun UpperPaneTopBars(
    actions: List<PaneTopBarActionButton>,
    onSearch: (String) -> Unit,
    onSearchNext: () -> Unit,
    onSearchPrevious: () -> Unit,
    currentSearchResultIndex: Int,
    totalSearchResultCount: Int,
    modifier: Modifier = Modifier,
) {
    var isSearchMode by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxWidth()) {
        UpperPaneTitleBar(
            actions = actions,
            isSearchMode = isSearchMode,
            onSearchClick = {
                if (isSearchMode) {
                    isSearchMode = false
                    searchQuery = ""
                    onSearch("")
                } else {
                    isSearchMode = true
                }
            },
        )

        AnimatedVisibility(
            visible = isSearchMode,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = {
                    searchQuery = it
                    onSearch(it)
                },
                onNext = onSearchNext,
                onPrevious = onSearchPrevious,
                currentIndex = currentSearchResultIndex,
                totalResults = totalSearchResultCount,
            )
        }
    }
}
