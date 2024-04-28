package viewmodel

import model.Inspector

class AppViewModel {
    private val inspector = Inspector()

    val isInspectorPopulated
        get() = inspector.isPopulated

    fun dumpXml() = inspector.dumpXml()
}