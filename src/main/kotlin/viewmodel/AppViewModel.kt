package viewmodel

import oldModel.LayoutInspector
import oldModel.notification.NotificationManager

class AppViewModel(
    private val layoutInspector: LayoutInspector,
    val notificationManager: NotificationManager
) {
    val buttonState = ButtonState(
        getInspectorState = { layoutInspector.state },
        extract = layoutInspector::extractLayout,
        notificationManager = notificationManager
    )

    val paneState = PaneState(
        getInspectorState = { layoutInspector.state },
        getDataRoot = { layoutInspector.data.root },
        isNodeSelected = { layoutInspector.isNodeSelected },
        getSelectedNode = { layoutInspector.selectedNode },
        selectNode = layoutInspector::selectNode
    )

    val screenshotState = ScreenshotState(
        getInspectorState = { layoutInspector.state },
        getScreenshotFile = { layoutInspector.data.screenshotFile },
        getDataRoot = { layoutInspector.data.root },
        isNodeSelected = { layoutInspector.isNodeSelected },
        getSelectedNode = { layoutInspector.selectedNode },
        selectNode = layoutInspector::selectNode
    )

    fun teardown() {
        layoutInspector.teardown()
    }
}