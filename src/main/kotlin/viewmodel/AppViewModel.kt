package viewmodel

import model.Inspector

class AppViewModel {
    val inspector = Inspector()

    fun dumpXml() {
        inspector.dumpXml()
    }
}