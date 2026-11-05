import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication

class UserSession
class SessionData
class CartRepository(val sessionData: SessionData)
class CheckoutPresenter(val cart: CartRepository)
class CheckoutNavigator

fun check() {
    val koin = koinApplication { modules(sessionModule) }.koin
    val scope = koin.createScope<UserSession>("session", UserSession())
    scope.get<CartRepository>()
    scope.get<CheckoutPresenter>()
    koin.createScope("checkout-1", named("checkout")).get<CheckoutNavigator>()
}
