import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import model.CoroutineManager
import shared.Dimensions.minimumWindowHeight
import shared.Dimensions.minimumWindowWidth
import view.MainScreen
import viewmodel.AppViewModel
import java.awt.Dimension

val AppViewModel = compositionLocalOf<AppViewModel> {
    error("No view model provided.")
}

fun main() = application {

    val viewModel = remember {
        AppViewModel(
            coroutineManager = CoroutineManager(
                customCoroutineScope = CoroutineScope(Dispatchers.IO + Job())
            )
        )
    }

    CompositionLocalProvider(AppViewModel provides viewModel) {

        Window(
            title = "Schaumamal",
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
    }
}