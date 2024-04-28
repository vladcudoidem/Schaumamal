import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import viewmodel.AppViewModel

val AppViewModel = compositionLocalOf<AppViewModel> {
    error("No ViewModel provided")
}

fun main() = application {
    val viewModel = remember { AppViewModel() }

    CompositionLocalProvider(AppViewModel provides viewModel) {
        Window(
            title = "Schaumamal",
            onCloseRequest = {
                viewModel.teardown()
                exitApplication()
            }
        ) {
            App()
        }
    }
}
