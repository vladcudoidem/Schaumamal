package model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import model.parser.xmlElements.Node
import model.parser.XmlParser
import model.utils.CoroutineManager
import model.extractionManagers.OldExtractionManager
import model.Paths.LOCAL_DUMP_PATH
import model.Paths.LOCAL_SCREENSHOT_PATH
import java.io.File

class LayoutInspector(
    private val coroutineManager: CoroutineManager
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
        state = InspectorState.WAITING

        // Reset selected node state and data.
        isNodeSelected = false
        selectedNode = Node.Empty

        coroutineManager.launch {
            // first part of the dump
            OldExtractionManager.extract()

            // second part of the dump
            data = LayoutData(
                screenshotFile = File(LOCAL_SCREENSHOT_PATH),
                root = XmlParser.parseSystem(File(LOCAL_DUMP_PATH))
            )

            // Shows data only after refreshing the data.
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
        // This might not be needed.
        // TeardownManager.deleteLayoutFiles()
    }
}