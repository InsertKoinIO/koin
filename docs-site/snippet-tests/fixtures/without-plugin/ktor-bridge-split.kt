import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import org.koin.ktor.ext.inject
import kotlin.test.assertEquals

class Database(val config: ApplicationConfig)
class CustomerRepository(val database: Database)
class CustomerService(val repository: CustomerRepository)

fun check() {
    testApplication {
        application {
            module()
            routing {
                get("/") {
                    val service: CustomerService by inject()
                    service.repository.database
                    call.respondText("ok")
                }
            }
        }
        assertEquals("ok", client.get("/").bodyAsText())
    }
}
