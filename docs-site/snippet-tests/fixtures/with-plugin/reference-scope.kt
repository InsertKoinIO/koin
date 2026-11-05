import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module
import org.koin.plugin.module.dsl.koinApplication
import org.koin.core.annotation.Singleton
import org.koin.core.qualifier.named

@Module
@ComponentScan("__PACKAGE__")
class ScanModule

@KoinApplication(configurations = ["__PACKAGE__"], modules = [ScanModule::class])
class ScanApp

class UserSession

@Singleton
class UserRepository

fun check() {
    val koin = koinApplication<ScanApp>().koin
    koin.createScope<UserSession>("session", UserSession()).get<SessionCache>()
    koin.createScope("checkout-1", named("checkout")).get<CheckoutState>()
}
