import io.ktor.client.request.get
import io.ktor.server.testing.testApplication
import org.koin.dsl.module
import kotlin.test.assertEquals

interface KtorSpecificService
class KtorSpecificServiceImpl : KtorSpecificService

val appModule = module { }

fun check() {
    testApplication {
        application { module() }
        assertEquals(404, client.get("/").status.value)
    }
}
