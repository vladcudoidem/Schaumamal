import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import shared.Dimensions.minimumWindowHeight
import shared.Dimensions.minimumWindowWidth
import view.MainScreen
import view.UiLayoutState
import view.onWindowKeyEvent
import viewmodel.AppViewModel
import viewmodel.ButtonState
import viewmodel.PaneState
import viewmodel.ScreenshotState
import java.awt.Dimension

fun main() = application {

    KoinApplication(
        application = {
            modules(
                viewModelModule,
                notificationModule,
                coroutineModule,
                extractionModule
            )
        }
    ) {
        val viewModel: AppViewModel = koinInject()
        val buttonState = remember {
            ButtonState(
                inspectorState = viewModel.state,
                extract = viewModel::extractLayout,
                notificationManager = viewModel.notificationManager
            )
        }
        val paneState = remember {
            PaneState(
                inspectorState = viewModel.state,
                data = viewModel.data,
                isNodeSelected = viewModel.isNodeSelected,
                selectedNode = viewModel.selectedNode,
                selectNode = viewModel::selectNode
            )
        }
        val screenshotState = remember {
            ScreenshotState(
                inspectorState = viewModel.state,
                isNodeSelected = viewModel.isNodeSelected,
                selectedNode = viewModel.selectedNode,
                data = viewModel.data,
                selectNode = viewModel::selectNode
            )
        }
        val uiLayoutState = remember {
            UiLayoutState(screenshotComposableSize = screenshotState.screenshotComposableSize)
        }

        val density = LocalDensity.current.density
        val isExtractButtonEnabled by buttonState.isExtractButtonEnabled.collectAsState(initial = true)
        val areResizeButtonsEnabled by buttonState.areResizeButtonsEnabled.collectAsState(initial = false)

        Window(
            title = "Schaumamal",
            icon = painterResource("appIcons/icon.png"),
            onCloseRequest = {
                viewModel.teardown()
                exitApplication()
            },
            onKeyEvent = {
                onWindowKeyEvent(
                    event = it,
                    isExtractButtonEnabled = isExtractButtonEnabled,
                    onExtractButtonPressed = buttonState::onExtractButtonPressed,
                    areResizeButtonsEnabled = areResizeButtonsEnabled,
                    onEnlargeScreenshotButtonPressed = uiLayoutState::onEnlargeScreenshotButtonPressed,
                    onShrinkScreenshotButtonPressed = uiLayoutState::onShrinkScreenshotButtonPressed,
                    onFitScreenshotToScreenButtonPressed = {
                        uiLayoutState.onFitScreenshotToScreenButtonPressed(density)
                    },
                )
            }
        ) {
            // This seems to be density-independent (i.e. values behave like Dp).
            window.minimumSize = Dimension(minimumWindowWidth, minimumWindowHeight)

            MaterialTheme {
                MainScreen(
                    screenshotState = screenshotState,
                    buttonState = buttonState,
                    paneState = paneState,
                    uiLayoutState = uiLayoutState,
                    notificationManager = viewModel.notificationManager
                )
            }
        }
    }
}
