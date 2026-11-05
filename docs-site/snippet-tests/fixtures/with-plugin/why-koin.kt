import kotlin.test.assertSame
import org.koin.dsl.koinApplication

class Database
class Logger
class UserRepository(val database: Database, val logger: Logger)
class UserService(val repository: UserRepository)

fun check() {
    assertSame(userRepository, userService.repository)

    val koin = koinApplication { modules(appModule) }.koin
    assertSame(koin.get<Database>(), koin.get<UserService>().repository.database)
}
