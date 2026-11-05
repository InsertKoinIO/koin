import org.koin.dsl.koinApplication
import kotlin.test.assertNull

class UserSession
class ApiService
class SessionData
class CartRepository(val api: ApiService, val sessionData: SessionData)

fun check() {
    val koin = koinApplication { modules(sessionModule) }.koin
    koin.createScope<UserSession>("session", UserSession()).get<CartRepository>()
    assertNull(koin.getOrNull<SessionData>(), "a scoped definition must not resolve from the root scope")
}
