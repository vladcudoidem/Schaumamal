package model.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class CoroutineManager {

    private val customCoroutineScope = CoroutineScope(Dispatchers.IO + Job())

    fun launch(block: suspend CoroutineScope.() -> Unit) =
        customCoroutineScope.launch(block = block)
        // TODO what happens if I launch two coroutines in the same scope?

    fun teardown() = customCoroutineScope.cancel()
}