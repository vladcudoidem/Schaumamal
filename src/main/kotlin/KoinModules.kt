import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import oldModel.CoroutineManager
import oldModel.LayoutInspector
import oldModel.extractionManagers.getExtractionManager
import oldModel.notification.NotificationManager
import org.koin.dsl.module
import viewmodel.AppViewModel

val viewModelModule = module {
    single { AppViewModel(get(), get()) }
}

val notificationModule = module {
    single { NotificationManager() }
}

val inspectorModule = module {
    single { LayoutInspector(get(), get()) }
}

val coroutineModule = module {
    factory { CoroutineScope(Dispatchers.IO + Job()) }
    factory { CoroutineManager(get()) }
}

val extractionModule = module {
    single { getExtractionManager() }
}