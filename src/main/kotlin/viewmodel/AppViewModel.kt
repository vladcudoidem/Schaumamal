package viewmodel

import model.Inspector

class AppViewModel {
    private val inspector = Inspector()

    val inspectorState
        get() = inspector.state

    fun dumpXml() = inspector.dumpXml()
}