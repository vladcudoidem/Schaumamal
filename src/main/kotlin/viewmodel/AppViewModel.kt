package viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import oldModel.CoroutineManager
import oldModel.InspectorState
import oldModel.LayoutData
import oldModel.extractionManagers.ExtractionManager
import oldModel.notification.NotificationManager
import oldModel.parser.XmlParser
import oldModel.parser.xmlElements.Node
import java.io.File

class AppViewModel(
    private val coroutineManager: CoroutineManager,
    private val extractionManager: ExtractionManager,
    val notificationManager: NotificationManager
) {
    // Todo: delete LayoutInspector.kt

    private val _state = MutableStateFlow(InspectorState.EMPTY)
    val state get() = _state.asStateFlow()

    private val _data = MutableStateFlow(LayoutData.Empty)
    val data get() = _data.asStateFlow()

    private val _isNodeSelected = MutableStateFlow(false)
    val isNodeSelected get() = _isNodeSelected.asStateFlow()

    private val _selectedNode = MutableStateFlow(Node.Empty)
    val selectedNode get() = _selectedNode.asStateFlow()

    fun extractLayout(onException: (Exception) -> Unit = { }) {
        // Save previous state so that we can return to it in case the extraction fails.
        val previousStateValue = _state.value  // Todo: use update()
        _state.value = InspectorState.WAITING

        coroutineManager.launch {
            // First we extract the new data.
            val (dumpPath, screenshotPath) = try {
                extractionManager.extract()
            } catch (e: Exception) {
                // In case of failure, revert the state back to its previous value. Because of the return statement at
                // the end of this block, the data and UI state remain unchanged.
                _state.value = previousStateValue

                onException(e)
                return@launch
            }

            // Then we parse and set the new data.
            _data.value = LayoutData(
                screenshotFile = File(screenshotPath),
                root = XmlParser.parseSystem(File(dumpPath))
            )

            _isNodeSelected.value = false
            _selectedNode.value = Node.Empty

            // At the end, show the data.
            _state.value = InspectorState.POPULATED
        }
    }

    fun selectNode(node: Node) {
        _selectedNode.value = node
        // Shows selected node only after refreshing the data.
        _isNodeSelected.value = true
    }

    // Todo: move this:

    val buttonState = ButtonState(
        inspectorState = state,
        extract = ::extractLayout,
        notificationManager = notificationManager
    )

    val paneState = PaneState(
        inspectorState = state,
        data = data,
        isNodeSelected = isNodeSelected,
        selectedNode = selectedNode,
        selectNode = ::selectNode
    )

    val screenshotState = ScreenshotState(
        inspectorState = state,
        isNodeSelected = isNodeSelected,
        selectedNode = selectedNode,
        data = data,
        selectNode = ::selectNode
    )

    fun teardown() {
        coroutineManager.teardown()
    }
}