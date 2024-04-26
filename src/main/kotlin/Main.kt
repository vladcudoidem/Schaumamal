import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.cancel
import model.Utils
import viewmodel.AppViewModel

val AppViewModel = compositionLocalOf<AppViewModel> {
    error("No ViewModel provided")
}

fun main() = application {
    Window(
        title = "Schaumamal",
        onCloseRequest = {
            generalTeardown()
            exitApplication()
        }
    ) {
        val viewModel = remember { AppViewModel() }

        CompositionLocalProvider(AppViewModel provides viewModel) {
            App()
        }
    }
}

fun generalTeardown() {
    Utils.customCoroutineScope.cancel()
    Utils.deleteFilesOnTeardown()
}
