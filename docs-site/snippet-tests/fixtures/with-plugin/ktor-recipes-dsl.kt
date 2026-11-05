import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.runBlocking
import org.koin.ktor.ext.getKoin

// Runs the recipes of ktor/configuration.mdx against an in-memory database.
fun check() {
    lateinit var pool: HikariDataSource
    runBlocking {
        testApplication {
            environment {
                config = MapApplicationConfig(
                    "database.url" to "jdbc:h2:mem:recipes",
                    "database.maxPoolSize" to "2",
                )
            }
            application {
                module()
                val koin = getKoin()
                check(koin.get<DatabaseConfig>().maxPoolSize == 2)
                koin.get<UserStore>()
                pool = koin.get<HikariDataSource>()
                check(koin.get<javax.sql.DataSource>() === pool)
                pool.connection.use { check(it.isValid(1)) }
                check(!pool.isClosed)
            }
            startApplication()
        }
    }
    check(pool.isClosed) { "The pool must be closed when the application stops" }
}
