import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import viewmodel.AppViewModel
import zlayground.Dashboard

val AppViewModel = compositionLocalOf<AppViewModel> {
    error("No view model provided.")
}

fun main() = application {

    Window(
        title = "Schaumamal",
        icon = painterResource("appIcons/icon.png"),
        onCloseRequest = ::exitApplication,
        state = WindowState(position = WindowPosition(alignment = Alignment.BottomEnd))
    ) {
        MaterialTheme {
            Dashboard()
        }
    }

    /*val viewModel = remember {
        AppViewModel(
            coroutineManager = CoroutineManager(
                customCoroutineScope = CoroutineScope(Dispatchers.IO + Job())
            )
        )
    }

    CompositionLocalProvider(AppViewModel provides viewModel) {

        Window(
            title = "Schaumamal",
            icon = painterResource("appIcons/icon.png"),
            onCloseRequest = {
                viewModel.teardown()
                exitApplication()
            },
            onKeyEvent = viewModel::onWindowKeyEvent
        ) {
            // This seems to be density-independent (i.e. values behave like Dp).
            window.minimumSize = Dimension(minimumWindowWidth, minimumWindowHeight)

            MaterialTheme {
                MainScreen()
            }
        }
    }*/
}