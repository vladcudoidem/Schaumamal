package viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import oldModel.CoroutineManager
import oldModel.InspectorState
import oldModel.LayoutInspector
import oldModel.notification.NotificationManager

class AppViewModel(
    private val layoutInspector: LayoutInspector,
    private val coroutineManager: CoroutineManager,
    val notificationManager: NotificationManager
) {
    val extractButtonState = ExtractButtonState(
        getInspectorState = { layoutInspector.state },
        extract = layoutInspector::extractLayout,
        notificationManager = notificationManager
    )

    val paneState = PaneState(
        getInspectorState = { layoutInspector.state },
        getDataRoot = { layoutInspector.data.root },
        isNodeSelected = { layoutInspector.isNodeSelected },
        getSelectedNode = { layoutInspector.selectedNode },
        selectNode = layoutInspector::selectNode,
        getDensity = { density },
        getPanesConstraint = { DpSize(width = panesWidthConstraint, height = panesHeightConstraint) },
        coroutineManager = coroutineManager
    )

    val screenshotState = ScreenshotState(
        getInspectorState = { layoutInspector.state },
        getScreenshotFile = { layoutInspector.data.screenshotFile },
        getDataRoot = { layoutInspector.data.root },
        isNodeSelected = { layoutInspector.isNodeSelected },
        getSelectedNode = { layoutInspector.selectedNode },
        selectNode = layoutInspector::selectNode,
        getDensity = { density },
        getPaneWidth = { paneState.paneWidth },
        getPanesConstraint = { DpSize(width = panesWidthConstraint, height = panesHeightConstraint) },
        scrollToSelectedNode = paneState::scrollToSelectedNode
    )

    private var density by mutableStateOf(Float.NaN)

    fun onNewDensity(density: Float) {
        this.density = density
    }

    // panesHeightConstraint and panesWidthConstraint are, under the current implementation, also the height and width
    // of the window content area (i.e. the total area, excluding the window bar). These values do not need to be states
    // as they are only used as limits in the wedge drag event handlers.
    private var panesHeightConstraint = Dp.Unspecified
    private var panesWidthConstraint = Dp.Unspecified

    fun onPanesHeightConstraintChanged(newHeightConstraint: Dp) {
        // First update the height constraint.
        panesHeightConstraint = newHeightConstraint

        // Then make sure that the upper pane height is within limits. This is relevant when the window gets resized
        // and the height constraint gets smaller, but the upper pane height stays the same.
        paneState.coercePaneHeight()
    }

    fun onPanesWidthConstraintChanged(newWidthConstraint: Dp) {
        // First update the width constraint.
        panesWidthConstraint = newWidthConstraint

        // Then make sure that the pane width is within limits. This is relevant when the window gets resized and the
        // width constraint gets smaller, but the pane width stays the same.
        paneState.coercePaneWidth()
    }

    val areResizeButtonsEnabled get() = layoutInspector.state == InspectorState.POPULATED

    @OptIn(ExperimentalComposeUiApi::class)
    fun onWindowKeyEvent(event: KeyEvent) =
        when {
            event.isCtrlPressed && event.type == KeyEventType.KeyDown -> {
                when (event.key) {
                    Key.D -> {
                        if (extractButtonState.isEnabled) {
                            extractButtonState.onButtonPressed()
                        }
                        true
                    }

                    Key.Period -> {
                        if (areResizeButtonsEnabled) {
                            screenshotState.onEnlargeScreenshotButtonPressed()
                        }
                        true
                    }

                    Key.Comma -> {
                        if (areResizeButtonsEnabled) {
                            screenshotState.onShrinkScreenshotButtonPressed()
                        }
                        true
                    }

                    Key.M -> {
                        if (areResizeButtonsEnabled) {
                            screenshotState.onFitScreenshotToScreenButtonPressed()
                        }
                        true
                    }

                    else -> false
                }
            }

            else -> false
        }

    fun teardown() {
        coroutineManager.teardown()
        layoutInspector.teardown()
    }
}