import kotlin.test.assertSame

class Database
class Logger
class DatabaseRepository(val database: Database, val logger: Logger)
class UserService(val repository: DatabaseRepository)
class UserApplication(val service: UserService)

fun check() {
    assertSame(database, application.service.repository.database)
}
