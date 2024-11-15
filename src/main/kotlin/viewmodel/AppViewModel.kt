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

    // Todo: set up shortcuts
/*    @OptIn(ExperimentalComposeUiApi::class)
    fun onWindowKeyEvent(event: KeyEvent) =
        when {
            event.isCtrlPressed && event.type == KeyEventType.KeyDown -> {
                when (event.key) {
                    Key.D -> {
                        if (buttonState.isExtractButtonEnabled) {
                            buttonState.onExtractButtonPressed()
                        }
                        true
                    }

                    Key.Period -> {
                        if (buttonState.areResizeButtonsEnabled) {
                            screenshotState.onEnlargeScreenshotButtonPressed()
                        }
                        true
                    }

                    Key.Comma -> {
                        if (buttonState.areResizeButtonsEnabled) {
                            screenshotState.onShrinkScreenshotButtonPressed()
                        }
                        true
                    }

                    Key.M -> {
                        if (buttonState.areResizeButtonsEnabled) {
                            screenshotState.onFitScreenshotToScreenButtonPressed()
                        }
                        true
                    }

                    else -> false
                }
            }

            else -> false
        }*/

    fun teardown() {
        layoutInspector.teardown()
    }
}