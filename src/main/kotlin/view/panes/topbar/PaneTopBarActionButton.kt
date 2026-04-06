package view.panes.topbar

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class PaneTopBarActionButton(
    val iconResource: String,
    val onClick: () -> Unit,
    val enabled: StateFlow<Boolean> = MutableStateFlow(true),
)
