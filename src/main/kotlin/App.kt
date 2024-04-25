import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import view.MainScreen
import viewmodel.AppViewModel

@Composable
@Preview
fun App() {
    val viewModel = remember { AppViewModel() }

    CompositionLocalProvider(LocalViewModel provides viewModel) {
        MaterialTheme {
            MainScreen()
        }
    }
}

val LocalViewModel = compositionLocalOf<AppViewModel> {
    error("No ViewModel provided")
}