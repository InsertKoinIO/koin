import kotlin.test.assertTrue
import org.koin.dsl.koinApplication

interface UserRepository {
    fun findName(): String
}

class UserRepositoryImpl : UserRepository {
    override fun findName() = "Alice"
}

class UserService(private val repository: UserRepository)

class UserApplication(private val service: UserService)

fun check() {
    val koin = koinApplication { modules(appModule) }.koin
    koin.get<UserApplication>()
    assertTrue(koin.get<UserRepository>() is UserRepositoryImpl)
}
