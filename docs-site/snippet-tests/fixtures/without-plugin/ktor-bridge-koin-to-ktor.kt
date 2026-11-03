import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.server.testing.testApplication
import org.koin.dsl.module
import kotlin.test.assertEquals

interface DatabaseConnection { fun query(): String }
class DatabaseConnectionImpl : DatabaseConnection { override fun query() = "row from Ktor DI" }

val appModule = module { }

fun check() {
    testApplication {
        application { module() }
        assertEquals("row from Ktor DI", client.get("/data").bodyAsText())
    }
}
