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
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module
import org.koin.plugin.module.dsl.withConfiguration

@Module
@ComponentScan("__PACKAGE__")
class RequestModule

@KoinApplication(configurations = ["__PACKAGE__"], modules = [RequestModule::class])
class RequestApp

fun check() {
    testApplication {
        application {
            install(Koin) { withConfiguration<RequestApp>() }
            routing {
                get("/") {
                    call.scope.get<RequestLogger>()
                    call.scope.get<UserSessionHandler>()
                    call.respondText("ok")
                }
            }
        }
        assertEquals("ok", client.get("/").bodyAsText())
    }
}
