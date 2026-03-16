package view.panes.toolbar

data class PaneToolbarAction(
    val iconResource: String,
    val onClick: () -> Unit,
)
