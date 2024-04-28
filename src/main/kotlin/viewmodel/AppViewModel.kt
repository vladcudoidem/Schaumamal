package viewmodel

import kotlinx.coroutines.cancel
import model.CoroutineHelper
import model.Inspector
import model.TeardownHelper

// TODO create folders at startup

class AppViewModel {
    private val inspector = Inspector()

    val layoutData
        get() = inspector.layoutData

    val isInspectorPopulated
        get() = inspector.isPopulated

    fun extractLayout() = inspector.extractLayout()

    fun teardown() {
        CoroutineHelper.customCoroutineScope.cancel()
        TeardownHelper.deleteLayoutFiles()
    }
}