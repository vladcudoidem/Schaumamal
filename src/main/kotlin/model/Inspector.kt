package model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import model.Utils.customCoroutineScope

class Inspector {
    var state by mutableStateOf(InspectorState.EMPTY)
        private set

    fun dumpXml() {
        state = InspectorState.WAITING

        customCoroutineScope.launch {
            Utils.dumpXml()
            Utils.takeScreenshot()

            state = InspectorState.POPULATED
        }
    }
}