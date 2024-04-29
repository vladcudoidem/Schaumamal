package model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.loadImageBitmap
import model.Constants.LOCAL_DUMP_PATH
import model.Constants.LOCAL_SCREENSHOT_PATH
import model.parser.XmlParser
import model.utils.CoroutineManager
import model.utils.LayoutManager
import model.utils.TeardownManager
import java.io.File
import java.io.FileInputStream

class LayoutInspector {
    private var state by mutableStateOf(InspectorState.EMPTY)
    lateinit var data: LayoutData
        private set

    val isPopulated
        get() = state == InspectorState.POPULATED

    fun extractLayout() {
        state = InspectorState.WAITING

        CoroutineManager.launch {
            LayoutManager.extract()

            data = LayoutData(
                loadImageBitmap(FileInputStream(File(LOCAL_SCREENSHOT_PATH))),
                XmlParser.parseSystem(File(LOCAL_DUMP_PATH))
            )
            state = InspectorState.POPULATED
        }
    }

    fun teardown() {
        CoroutineManager.teardown()
        TeardownManager.deleteLayoutFiles()
    }
}