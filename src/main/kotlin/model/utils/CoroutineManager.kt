package model.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

object CoroutineManager {

    private val customCoroutineScope = CoroutineScope(Dispatchers.IO + Job())

    fun launch(block: () -> Unit) = customCoroutineScope.launch { block() }
        // TODO what happens if I launch two coroutines in the same scope?

    fun teardown() = customCoroutineScope.cancel()
}