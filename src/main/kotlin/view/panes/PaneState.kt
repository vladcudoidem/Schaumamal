package view.panes

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import model.InspectorState
import model.displayDataResolver.DisplayData
import model.parser.dataClasses.GenericNode
import model.parser.dataClasses.Node
import view.utils.getFlatXmlTreeMap
import view.utils.propertyMap

class PaneState(
    inspectorState: StateFlow<InspectorState>,
    displayData: StateFlow<DisplayData>,
    isNodeSelected: StateFlow<Boolean>,
    val selectedNode: StateFlow<GenericNode>,
    val selectNode: (GenericNode) -> Unit,
    searchResult: Flow<List<GenericNode>>,
    searchQuery: StateFlow<String>,
    val onSearchQueryChanged: (String) -> Unit,
    isSearchActive: StateFlow<Boolean>,
    val toggleSearchActive: () -> Unit,
    shouldHighlightResultsOnScreenshot: StateFlow<Boolean>,
    val toggleHighlightResultsOnScreenshot: () -> Unit,
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    val showXmlTree = inspectorState.map { it == InspectorState.POPULATED }
    private val flatXmlTreeMap =
        displayData
            .map { it.displayNode.getFlatXmlTreeMap { node: GenericNode -> selectNode(node) } }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.Eagerly,
                initialValue = linkedMapOf(),
            )

    // Todo: could I use a list of pairs here?
    val flatXmlTree = flatXmlTreeMap.map { it.values.toList() }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val flatVisibleXmlTreeMap: Flow<Map<Node, XmlTreeLine>> =
        flatXmlTreeMap.flatMapLatest { currentTreeMap ->
            if (currentTreeMap.isEmpty()) {
                return@flatMapLatest flowOf(emptyMap())
            }

            val nodeLinePairs = currentTreeMap.toList()
            val visibilityStates = nodeLinePairs.map { (_, line) -> line.isVisible }

            combine(visibilityStates) { visibilityStates ->
                buildMap {
                    nodeLinePairs.forEachIndexed { index, (node, line) ->
                        val isVisible = visibilityStates[index]
                        if (isVisible) {
                            put(node, line)
                        }
                    }
                }
            }
        }

    // Todo: when having a node selected and collapsing or expanding some lines above it, we
    //  automatically scroll to the selected node. Fix this. We should probably only automatically
    //  scroll when the selected node changes. Or maybe not automatically scroll, only manually when
    //  sending the event. We can ues "onEach { ... }" and stuff like that.
    private val selectedNodeIndex =
        combine(flatVisibleXmlTreeMap, selectedNode) { flatVisibleXmlTreeMap, selectedNode ->
                flatVisibleXmlTreeMap.keys.indexOf(selectedNode)
            }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.Eagerly,
                // Todo: this default is ok currently because we only scroll when we are allowed
                initialValue = 0,
            )
    // Todo: remove this and use nullable states or something...
    private val isTreeScrollAllowed =
        combine(inspectorState, isNodeSelected) { inspectorState, isNodeSelected ->
            inspectorState == InspectorState.POPULATED && isNodeSelected
        }

    private val automaticTreeScrollEvents =
        selectedNodeIndex.map { TreeScrollEvent(targetIndex = it) }
    private val manualTreeScrollEvents = MutableSharedFlow<TreeScrollEvent>(extraBufferCapacity = 1)

    val combinedTreeScrollEvents =
        merge(automaticTreeScrollEvents, manualTreeScrollEvents).combineTransform(
            isTreeScrollAllowed
        ) { scrollEvent, isScrollAllowed ->
            // Should not be the case, but better safe than sorry.
            val isIndexValid = scrollEvent.targetIndex != -1

            if (isScrollAllowed && isIndexValid) {
                emit(scrollEvent)
            }
        }

    val showSelectedNodeProperties =
        combine(showXmlTree, isNodeSelected) { showXmlTree, isNodeSelected ->
            showXmlTree && isNodeSelected
        }
    val selectedNodePropertyMap = selectedNode.map { it.propertyMap }

    val isExpandAndScrollButtonEnabled = isNodeSelected

    val searchQuery = searchQuery

    val isSearchActive = isSearchActive

    val shouldHighlightResultsOnScreenshot = shouldHighlightResultsOnScreenshot

    val searchResult =
        searchResult.stateIn(
            scope = coroutineScope,
            started = SharingStarted.Lazily,
            initialValue = listOf(),
        )

    val searchResultText =
        combine(this.searchResult, selectedNode) { searchResult, selectedNode ->
            val indexOfSelectedNodeInSearchResults =
                searchResult.indexOf(selectedNode).takeIf { it != -1 }
            val searchResultCount = searchResult.size

            if (searchResult.isEmpty()) {
                "0 matches"
            } else if (indexOfSelectedNodeInSearchResults != null) {
                "${indexOfSelectedNodeInSearchResults + 1}/$searchResultCount"
            } else {
                // The selected node is not a search result.
                "-/$searchResultCount"
            }
        }

    init {
        collectSelectedNodes()
        collectSearchResult()
    }

    private fun collectSelectedNodes() {
        coroutineScope.launch {
            var lastSelectedNode: GenericNode? = null

            selectedNode
                .transform {
                    val newSelectedNodes = SelectedNodes(current = it, last = lastSelectedNode)
                    lastSelectedNode = it

                    emit(newSelectedNodes)
                }
                .collect { propagateNodeSelection(it) }
        }
    }

    private fun collectSearchResult() {
        coroutineScope.launch {
            var lastSearchResult: List<GenericNode>? = null

            searchResult
                .onEach {
                    // We want to scroll to the first search match every time new matches are
                    // available.

                    val firstResult = it.firstOrNull()
                    if (firstResult != null) {
                        selectNode(firstResult)
                    }
                }
                .transform {
                    val newSearchResults = SearchResults(current = it, last = lastSearchResult)
                    lastSearchResult = it

                    emit(newSearchResults)
                }
                .collect { propagateSearchResults(it) }
        }
    }

    // Select and expands to the new selected node and deselect the old one.
    private fun propagateNodeSelection(selectedNodes: SelectedNodes) {
        val treeLineToSelect = flatXmlTreeMap.value[selectedNodes.current]

        val lastSelectedNode = selectedNodes.last
        val treeLineToDeselect =
            if (lastSelectedNode != null) {
                flatXmlTreeMap.value[selectedNodes.last]
            } else {
                null
            }

        treeLineToSelect?.apply {
            expandUntilVisible()
            select()
        }
        treeLineToDeselect?.deselect()
    }

    // Highlight the most recent search results.
    private fun propagateSearchResults(searchResults: SearchResults) {
        val effectiveLastSearchResult = searchResults.last?.toSet() ?: emptySet()
        val currentSearchResult = searchResults.current.toSet()

        val nodesToHighlight = currentSearchResult - effectiveLastSearchResult
        val nodesToUnhighlight = effectiveLastSearchResult - currentSearchResult

        val linesToHighlight = nodesToHighlight.mapNotNull { flatXmlTreeMap.value[it] }
        linesToHighlight.forEach { it.highlightAsSearchResult() }

        val linesToUnhighlight = nodesToUnhighlight.mapNotNull { flatXmlTreeMap.value[it] }
        linesToUnhighlight.forEach { it.removeSearchResultHighlight() }
    }

    fun collapseAllLines() {
        val lines = flatXmlTreeMap.value.values
        lines.forEach {
            if (it.isCollapsible) {
                it.collapse()
            }
        }
    }

    fun expandAllLines() {
        val allLines = flatXmlTreeMap.value.values
        allLines.forEach { it.expand() }
    }

    // Todo: fix (auto and manual) scrolling. We are currently not waiting for the UI to be updated.
    //  This can lead to race conditions and to scrolling not working properly.

    fun expandAndScrollToSelectedNode() {
        val selectedLine = flatXmlTreeMap.value[selectedNode.value]
        selectedLine?.expandUntilVisible()

        val targetIndex = selectedNodeIndex.value
        manualTreeScrollEvents.tryEmit(TreeScrollEvent(targetIndex))
    }

    fun selectNeighboringSearchResult(goInReverse: Boolean = false) {
        val nodes = flatXmlTreeMap.value.keys.toList()

        val indexToSearchResultPairs =
            searchResult.value
                .map { nodes.indexOf(it) to it }
                .filter {
                    // Should never be the case, only for runtime safety.
                    it.first != -1
                }
        val indexOfSelectedNode = nodes.indexOf(selectedNode.value).takeIf { it != -1 } ?: return

        val destinationSearchResult =
            indexToSearchResultPairs
                .run {
                    if (goInReverse) {
                        lastOrNull { it.first < indexOfSelectedNode } ?: lastOrNull()
                    } else {
                        firstOrNull { it.first > indexOfSelectedNode } ?: firstOrNull()
                    } ?: return
                }
                .second

        selectNode(destinationSearchResult)
    }

    fun selectNextSearchResult() {
        selectNeighboringSearchResult()
    }

    fun selectPreviousSearchResult() {
        selectNeighboringSearchResult(goInReverse = true)
    }
}

private data class SelectedNodes(val current: GenericNode, val last: GenericNode?)

private data class SearchResults(val current: List<GenericNode>, val last: List<GenericNode>?)

data class TreeScrollEvent(val targetIndex: Int)
