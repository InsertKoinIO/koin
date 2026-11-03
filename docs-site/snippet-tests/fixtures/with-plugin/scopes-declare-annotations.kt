import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module
import org.koin.plugin.module.dsl.koinApplication
import org.koin.core.qualifier.named

@Module
@ComponentScan("__PACKAGE__")
class ScanModule

@KoinApplication(configurations = ["__PACKAGE__"], modules = [ScanModule::class])
class ScanApp

class UserSession

fun check() {
    val koin = koinApplication<ScanApp>().koin
    val scope = koin.createScope<UserSession>("session", UserSession())
    scope.get<CartRepository>()
    scope.get<CheckoutPresenter>()
    koin.createScope("checkout-1", named("checkout")).get<CheckoutNavigator>()
}
