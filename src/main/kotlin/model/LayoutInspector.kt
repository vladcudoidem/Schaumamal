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
        state = InspectorState.WAITING

        // Reset selected node state and data.
        isNodeSelected = false
        selectedNode = Node.Empty

        coroutineManager.launch {
            // first part of the dump
            val (dumpPath, screenshotPath) = extractionManager.extract()

            // second part of the dump
            data = LayoutData(
                screenshotFile = File(screenshotPath),
                root = XmlParser.parseSystem(File(dumpPath))
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
    }
}