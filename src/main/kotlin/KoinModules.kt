import com.android.adblib.AdbSessionHost
import model.DisplayDataResolver
import model.dumper.Dumper
import model.dumper.NicknameProvider
import model.parser.XmlParser
import model.platform.PlatformInformationProvider
import model.repository.AppRepository
import oldModel.notification.NotificationManager
import org.koin.dsl.module
import viewmodel.AppViewModel

val viewModelModule = module {
    single { AppViewModel(get(), get(), get(), get()) }
    single { NotificationManager() }
    single { Dumper(get(), get(), get()) }
    single { AppRepository(get()) }
    single { DisplayDataResolver(get(), get()) }

    single { PlatformInformationProvider.current() }
    single { AdbSessionHost() }
    single { NicknameProvider() }

    single { XmlParser() }
}