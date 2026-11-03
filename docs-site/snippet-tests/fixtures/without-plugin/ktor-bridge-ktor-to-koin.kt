import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.server.testing.testApplication
import kotlin.test.assertEquals

interface HelloService { fun sayHello(): String }
class HelloServiceImpl : HelloService { override fun sayHello() = "hello from Koin" }

fun check() {
    testApplication {
        application { module() }
        assertEquals("hello from Koin", client.get("/hello").bodyAsText())
    }
}
