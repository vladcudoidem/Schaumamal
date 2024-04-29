package model.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

object CoroutineManager {
    private val customCoroutineScope = CoroutineScope(Dispatchers.IO + Job())

    fun launch(block: () -> Unit) = customCoroutineScope.launch { block() }

    fun teardown() = customCoroutineScope.cancel()
}