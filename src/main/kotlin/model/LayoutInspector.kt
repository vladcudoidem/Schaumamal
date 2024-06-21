package model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import model.extractionManagers.ExtractionManager
import model.extractionManagers.getExtractionManager
import model.parser.XmlParser
import model.parser.xmlElements.Node
import model.utils.CoroutineManager
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

    private val extractionManager: ExtractionManager = getExtractionManager()

    fun extractLayout() {
        state = InspectorState.WAITING

        // Reset selected node state and data.
        isNodeSelected = false
        selectedNode = Node.Empty

        // first part of the dump
        val dataPaths = extractionManager.extract()

        // second part of the dump
        data = LayoutData(
            screenshotFile = File(dataPaths.localScreenshotPath),
            root = XmlParser.parseSystem(File(dataPaths.localXmlDumpPath))
        )

        // Shows data only after refreshing the data.
        state = InspectorState.POPULATED

        // TODO use coroutine again (removed for debugging purposes)
        /*coroutineManager.launch {
            // first part of the dump
            val dataPaths = MacosExtractionManager.extract()

            // second part of the dump
            data = LayoutData(
                screenshotFile = File(dataPaths.localScreenshotPath),
                root = XmlParser.parseSystem(File(dataPaths.localXmlDumpPath))
            )

            // Shows data only after refreshing the data.
            state = InspectorState.POPULATED
        }*/
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