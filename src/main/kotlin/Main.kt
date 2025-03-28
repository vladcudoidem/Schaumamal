import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.awt.Dimension
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import shared.Dimensions.minimumWindowHeight
import shared.Dimensions.minimumWindowWidth
import view.MainScreen
import view.UiLayoutState
import view.button.ButtonState
import view.floatingWindow.FloatingWindowState
import view.notification.NotificationState
import view.panes.PaneState
import view.screenshot.ScreenshotState
import viewmodel.AppViewModel

fun main() = application {
    KoinApplication(application = { modules(viewModelModule) }) {
        val viewModel: AppViewModel = koinInject()
        val floatingWindowState = remember {
            FloatingWindowState(
                selectedDump = viewModel.selectedDump,
                resolvedDumpThumbnails = viewModel.resolvedDumpThumbnails,
                selectDump = viewModel::selectDump,
            )
        }
        val buttonState = remember {
            ButtonState(
                inspectorState = viewModel.state,
                selectedDump = viewModel.selectedDump,
                displayIndex = viewModel.displayIndex,
                displayCount = viewModel.displayCount,
                extract = viewModel::extract,
                switchDisplay = viewModel::switchDisplay,
                openDumpHistory = floatingWindowState::openDumpHistory,
            )
        }
        val paneState = remember {
            PaneState(
                inspectorState = viewModel.state,
                displayData = viewModel.selectedDisplayData,
                isNodeSelected = viewModel.isNodeSelected,
                selectedNode = viewModel.selectedNode,
                selectNode = viewModel::selectNode,
            )
        }
        // Todo: take layout stuff out of ScreenshotState and split UiLayoutState into two state
        // holders
        val screenshotState = remember {
            ScreenshotState(
                inspectorState = viewModel.state,
                isNodeSelected = viewModel.isNodeSelected,
                selectedNode = viewModel.selectedNode,
                displayData = viewModel.selectedDisplayData,
                selectNode = viewModel::selectNode,
            )
        }
        val uiLayoutState = remember {
            UiLayoutState(screenshotComposableSize = screenshotState.screenshotComposableSize)
        }
        val notificationState =
            NotificationState(notifications = viewModel.notificationManager.notifications)

        val density = LocalDensity.current.density
        val isExtractButtonEnabled by
            buttonState.isExtractButtonEnabled.collectAsState(initial = true)
        val areResizeButtonsEnabled by
            buttonState.areResizeButtonsEnabled.collectAsState(initial = false)

        Window(
            title = "Schaumamal",
            icon = painterResource("appIcons/icon.png"),
            onCloseRequest = {
                viewModel.cleanup()
                exitApplication()
            },
            onKeyEvent = {
                onWindowKeyEvent(
                    event = it,
                    isExtractButtonEnabled = isExtractButtonEnabled,
                    onExtractButtonPressed = buttonState::onExtractButtonPressed,
                    areResizeButtonsEnabled = areResizeButtonsEnabled,
                    onEnlargeScreenshotButtonPressed =
                        uiLayoutState::onEnlargeScreenshotButtonPressed,
                    onShrinkScreenshotButtonPressed =
                        uiLayoutState::onShrinkScreenshotButtonPressed,
                    onFitScreenshotToScreenButtonPressed = {
                        uiLayoutState.onFitScreenshotToScreenButtonPressed(density)
                    },
                )
            },
        ) {
            // This seems to be density-independent (i.e. values behave like Dp).
            window.minimumSize = Dimension(minimumWindowWidth, minimumWindowHeight)

            MaterialTheme {
                val customTextStyle =
                    LocalTextStyle.current.copy(
                        lineHeightStyle =
                            LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.FirstLineTop,
                            ),
                        fontSize = 14.sp,
                    )

                CompositionLocalProvider(LocalTextStyle provides customTextStyle) {
                    MainScreen(
                        screenshotState = screenshotState,
                        buttonState = buttonState,
                        paneState = paneState,
                        uiLayoutState = uiLayoutState,
                        floatingWindowState = floatingWindowState,
                        notificationState = notificationState,
                    )
                }
            }
        }
    }
}
