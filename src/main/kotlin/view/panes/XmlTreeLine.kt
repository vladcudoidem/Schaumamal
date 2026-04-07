package view.panes

import androidx.compose.ui.text.AnnotatedString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class XmlTreeLine(
    val text: AnnotatedString,
    val depth: Int,
    val onClickText: () -> Unit,
    val parentLine: XmlTreeLine? = null,
    val isCollapsible: Boolean = true,
) {
    // Todo: tidy up coroutines. Make use of structured concurrency.
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val _isCollapsed: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isCollapsed: StateFlow<Boolean> = _isCollapsed.asStateFlow()

    val isVisible: StateFlow<Boolean> =
        combine(
                flow = parentLine?.isCollapsed ?: MutableStateFlow(false),
                flow2 = parentLine?.isVisible ?: MutableStateFlow(true),
            ) { parentIsCollapsed, parentIsVisible ->
                parentIsVisible && !parentIsCollapsed
            }
            .stateIn(scope = coroutineScope, started = SharingStarted.Eagerly, initialValue = true)

    fun collapse() {
        _isCollapsed.update { true }
    }

    fun expand() {
        _isCollapsed.update { false }
    }

    fun expandUntilVisible() {
        val parentsFromTop = generateSequence(parentLine) { it.parentLine }.toList().asReversed()

        parentsFromTop.forEach { it.expand() }
    }

    private val _isSelected: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isSelected = _isSelected.asStateFlow()

    fun select() {
        _isSelected.update { true }
    }

    fun deselect() {
        _isSelected.update { false }
    }
}
