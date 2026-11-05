import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.server.application.install
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import org.koin.ktor.plugin.Koin
import org.koin.ktor.plugin.scope
import kotlin.test.assertEquals

class UserRepository

fun check() {
    testApplication {
        application {
            install(Koin) { modules(appModule) }
            routing {
                get("/") {
                    call.scope.get<RequestLogger>().log("called")
                    call.scope.get<UserSessionHandler>().userId()
                    call.respondText("ok")
                }
            }
        }
        assertEquals("ok", client.get("/").bodyAsText())
    }
}
