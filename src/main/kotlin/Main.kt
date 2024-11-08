import androidx.compose.material.MaterialTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import shared.Dimensions.minimumWindowHeight
import shared.Dimensions.minimumWindowWidth
import view.MainScreen
import viewmodel.AppViewModel
import java.awt.Dimension

fun main() = application {

    KoinApplication(
        application = {
            modules(
                viewModelModule,
                inspectorModule,
                coroutineModule,
                extractionModule
            )
        }
    ) {
        val viewModel: AppViewModel = koinInject()

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
    }
}