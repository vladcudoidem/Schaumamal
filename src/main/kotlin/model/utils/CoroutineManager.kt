package model.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

// The CoroutineManager is a class and not an object because it is stateful (the coroutine scope).
class CoroutineManager(
    private val customCoroutineScope: CoroutineScope
) {
    // TODO what happens if I launch two coroutines in the same scope?
    fun launch(block: suspend CoroutineScope.() -> Unit) =
        customCoroutineScope.launch(block = block)

    fun teardown() = customCoroutineScope.cancel()
}