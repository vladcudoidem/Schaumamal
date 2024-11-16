import androidx.compose.material.MaterialTheme
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
import java.awt.Dimension

fun main() = application {

    KoinApplication(
        application = {
            modules(
                viewModelModule,
                notificationModule,
                inspectorModule,
                coroutineModule,
                extractionModule
            )
        }
    ) {
        val density = LocalDensity.current.density

        val viewModel: AppViewModel = koinInject()
        val uiLayoutState = remember {
            UiLayoutState(getScreenshotComposableSize = { viewModel.screenshotState.screenshotComposableSize })
        }

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
                    buttonState = viewModel.buttonState,
                    uiLayoutState = uiLayoutState,
                    density = density
                )
            }
        ) {
            // This seems to be density-independent (i.e. values behave like Dp).
            window.minimumSize = Dimension(minimumWindowWidth, minimumWindowHeight)

            MaterialTheme {
                MainScreen(
                    viewModel = viewModel,
                    uiLayoutState = uiLayoutState
                )
            }
        }
    }
}
