package model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.loadImageBitmap
import model.Constants.LOCAL_SCREENSHOT_PATH
import java.io.File
import java.io.FileInputStream

class Inspector {
    private var state by mutableStateOf(InspectorState.EMPTY)
    lateinit var layoutData: LayoutData

    val isPopulated
        get() = state == InspectorState.POPULATED

    fun extractLayout() {
        state = InspectorState.WAITING

        CoroutineHelper.launch {
            LayoutExtractor.extract()

            layoutData = LayoutData(
                loadImageBitmap(FileInputStream(File(LOCAL_SCREENSHOT_PATH))),
                "RootNode"
            )
            state = InspectorState.POPULATED
        }
    }
}