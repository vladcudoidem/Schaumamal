import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type

@OptIn(ExperimentalComposeUiApi::class)
fun onWindowKeyEvent(
    event: KeyEvent,
    isExtractButtonEnabled: Boolean,
    onExtractButtonPressed: () -> Unit,
    areResizeButtonsEnabled: Boolean,
    onEnlargeScreenshotButtonPressed: () -> Unit,
    onShrinkScreenshotButtonPressed: () -> Unit,
    onFitScreenshotToScreenButtonPressed: () -> Unit
) =
    when {
        event.isCtrlPressed && event.type == KeyEventType.KeyDown -> {
            when (event.key) {
                Key.D -> {
                    if (isExtractButtonEnabled) {
                        onExtractButtonPressed()
                    }
                    true
                }

                Key.Period -> {
                    if (areResizeButtonsEnabled) {
                        onEnlargeScreenshotButtonPressed()
                    }
                    true
                }

                Key.Comma -> {
                    if (areResizeButtonsEnabled) {
                        onShrinkScreenshotButtonPressed()
                    }
                    true
                }

                Key.M -> {
                    if (areResizeButtonsEnabled) {
                        onFitScreenshotToScreenButtonPressed()
                    }
                    true
                }

                else -> false
            }
        }

        else -> false
    }