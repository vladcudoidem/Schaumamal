package viewmodel

import model.LayoutInspector

// TODO create folders at startup

class AppViewModel {
    private val layoutInspector = LayoutInspector()

    val layoutData
        get() = layoutInspector.data

    val isInspectorPopulated
        get() = layoutInspector.isPopulated

    fun extractLayout() = layoutInspector.extractLayout()

    fun teardown() {
        layoutInspector.teardown()
    }
}