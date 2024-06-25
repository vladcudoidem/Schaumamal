package model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import model.extractionManagers.ExtractionManager
import model.parser.XmlParser
import model.parser.xmlElements.Node
import java.io.File

class LayoutInspector(
    private val coroutineManager: CoroutineManager,
    private val extractionManager: ExtractionManager
) {
    var state by mutableStateOf(InspectorState.EMPTY)
        private set

    var data by mutableStateOf(LayoutData.Empty)
        private set

    var isNodeSelected by mutableStateOf(false)
        private set
    var selectedNode by mutableStateOf(Node.Empty)
        private set

    fun extractLayout() {
        // Save previous state so that we can return to it in case the extraction fails.
        val previousState = state
        state = InspectorState.WAITING

        coroutineManager.launch {
            // First we extract the new data.
            val (dumpPath, screenshotPath) = try {
                extractionManager.extract()
            } catch (e: Exception) {
                // In case of failure, revert the state back to its previous value. Because of the return statement at
                // the end of this block, the data and UI state remain unchanged.
                state = previousState

                // Todo: notify the use in case of failure.
                return@launch
            }

            // Then we parse and set the new data.
            data = LayoutData(
                screenshotFile = File(screenshotPath),
                root = XmlParser.parseSystem(File(dumpPath))
            )

            isNodeSelected = false
            selectedNode = Node.Empty

            // At the end, show the data.
            state = InspectorState.POPULATED
        }
    }

    fun selectNode(node: Node) {
        selectedNode = node
        // Shows selected node only after refreshing the data.
        isNodeSelected = true
    }

    fun teardown() {
        coroutineManager.teardown()
    }
}