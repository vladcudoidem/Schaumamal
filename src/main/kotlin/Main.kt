import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.application
import java.awt.Dimension
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.standalone.theme.default
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.dark
import org.jetbrains.jewel.intui.window.styling.defaults
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.styling.DecoratedWindowStyle
import org.jetbrains.jewel.window.styling.TitleBarMetrics
import org.jetbrains.jewel.window.styling.TitleBarStyle
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

        IntUiTheme(
            theme = JewelTheme.darkThemeDefinition(),
            styling =
                ComponentStyling.default()
                    .decoratedWindow(
                        windowStyle = DecoratedWindowStyle.dark(),
                        titleBarStyle =
                            TitleBarStyle.dark(
                                metrics =
                                    TitleBarMetrics.defaults(
                                        gradientStartX = (-300).dp,
                                        gradientEndX = 300.dp,
                                    )
                            ),
                    ),
        ) {
            DecoratedWindow(
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

                Box {
                    var titleBarHeight by remember { mutableStateOf(0.dp) }

                    Column {
                        Spacer(modifier = Modifier.height(titleBarHeight))

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

                    TitleBar(gradientStartColor = Color(0xFF5D2D30)) {
                        BoxWithConstraints {
                            LaunchedEffect(maxHeight) { titleBarHeight = maxHeight }

                            Row(horizontalArrangement = Arrangement.Center) {
                                Text(
                                    text = "Schaumamal",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
