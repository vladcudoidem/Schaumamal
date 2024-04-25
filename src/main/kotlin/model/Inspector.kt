package model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class Inspector {
    var state by mutableStateOf(InspectorState.EMPTY)
        private set

    fun dumpXml() {
        val customScope = CoroutineScope(Dispatchers.IO + Job())
        customScope.launch {
            Utils.dumpXml()
            Utils.takeScreenshot()
        }

        state = InspectorState.POPULATED
    }

    fun resetState() {
        state = InspectorState.EMPTY
    }
}