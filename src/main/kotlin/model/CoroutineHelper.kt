package model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object CoroutineHelper {
    val customCoroutineScope = CoroutineScope(Dispatchers.IO + Job())

    fun launch(block: () -> Unit) = customCoroutineScope.launch { block() }
}