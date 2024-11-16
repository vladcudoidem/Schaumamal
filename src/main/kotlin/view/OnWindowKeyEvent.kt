package view

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import viewmodel.ButtonState

@OptIn(ExperimentalComposeUiApi::class)
fun onWindowKeyEvent(
    event: KeyEvent,
    buttonState: ButtonState,
    uiLayoutState: UiLayoutState,
    density: Float
) =
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
                        uiLayoutState.onEnlargeScreenshotButtonPressed()
                    }
                    true
                }

                Key.Comma -> {
                    if (buttonState.areResizeButtonsEnabled) {
                        uiLayoutState.onShrinkScreenshotButtonPressed()
                    }
                    true
                }

                Key.M -> {
                    if (buttonState.areResizeButtonsEnabled) {
                        uiLayoutState.onFitScreenshotToScreenButtonPressed(density)
                    }
                    true
                }

                else -> false
            }
        }

        else -> false
    }