import org.koin.core.context.startKoin
import org.koin.dsl.module

class UserRepository

fun check() {
    startKoin { modules(module { single { UserRepository() } }) }
    UserService()
}
