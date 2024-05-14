package model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import model.parser.Node
import model.parser.XmlParser
import model.utils.CommandConstants.LOCAL_DUMP_PATH
import model.utils.CommandConstants.LOCAL_SCREENSHOT_PATH
import model.utils.CoroutineManager
import model.utils.LayoutManager
import model.utils.TeardownManager
import java.io.File

class LayoutInspector {

    var state by mutableStateOf(InspectorState.EMPTY)
        private set
    var data by mutableStateOf(LayoutData.empty)
        private set

    var isNodeSelected by mutableStateOf(false)
        private set
    var selectedNode by mutableStateOf(Node.empty)
        private set

    fun extractLayout() {
        state = InspectorState.WAITING

        // reset selected node
        isNodeSelected = false
        selectedNode = Node.empty

        CoroutineManager.launch {
            // first part of the dump
            LayoutManager.extract()

            // second part of the dump
            data = LayoutData(
                LOCAL_SCREENSHOT_PATH,
                XmlParser.parseSystem(File(LOCAL_DUMP_PATH))
            )

            // trigger recomposition
            state = InspectorState.POPULATED
        }
    }

    fun selectNode(node: Node) {
        selectedNode = node
        isNodeSelected = true
    }

    fun teardown() {
        CoroutineManager.teardown()
        TeardownManager.deleteLayoutFiles()
    }
}