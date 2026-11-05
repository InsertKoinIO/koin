import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.server.testing.testApplication
import kotlin.test.assertEquals

fun check() {
    testApplication {
        application { module() }
        assertEquals("Hello 'Alice' (alice@example.com)!", client.get("/hello").bodyAsText())
        assertEquals("Hello 'Bob' (bob@example.com)!", client.get("/hello?name=Bob").bodyAsText())
        assertEquals("User not found", client.get("/hello?name=Nobody").bodyAsText())
    }
}
