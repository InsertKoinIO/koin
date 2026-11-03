import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module
import org.koin.plugin.module.dsl.koinApplication
import kotlin.test.assertNull

@Module
@ComponentScan("__PACKAGE__")
class ScanModule

@KoinApplication(configurations = ["__PACKAGE__"], modules = [ScanModule::class])
class ScanApp

class UserSession

fun check() {
    val koin = koinApplication<ScanApp>().koin
    koin.createScope<UserSession>("session", UserSession()).get<CartRepository>()
    assertNull(koin.getOrNull<SessionData>(), "a scoped definition must not resolve from the root scope")
}
