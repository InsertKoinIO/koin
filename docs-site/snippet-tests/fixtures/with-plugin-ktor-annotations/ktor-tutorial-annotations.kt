// package: org.koin.sample
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.server.testing.testApplication
import kotlin.test.assertEquals
import org.koin.core.annotation.Singleton

// The page shows these classes as stubs with @Singleton on the classes written earlier.
interface UserRepository {
    fun findUserOrNull(name: String): User?
    fun addUsers(users: List<User>)
}

@Singleton
class UserRepositoryImpl : UserRepository {
    private val _users = arrayListOf<User>()
    override fun findUserOrNull(name: String): User? = _users.firstOrNull { it.name == name }
    override fun addUsers(users: List<User>) { _users.addAll(users) }
}

interface UserService {
    fun getUserOrNull(name: String = DEFAULT_USER.name): User?
    fun loadUsers()
    fun prepareHelloMessage(user: User?): String
}

@Singleton
class UserServiceImpl(private val userRepository: UserRepository) : UserService {
    override fun getUserOrNull(name: String): User? = userRepository.findUserOrNull(name)
    override fun loadUsers() { userRepository.addUsers(DEFAULT_USERS) }
    override fun prepareHelloMessage(user: User?): String =
        user?.let { "Hello '${user.name}' (${user.email})!" } ?: "User not found"
}

fun check() {
    testApplication {
        application { module() }
        assertEquals("Hello 'Alice' (alice@example.com)!", client.get("/hello").bodyAsText())
        assertEquals("Hello 'Bob' (bob@example.com)!", client.get("/hello?name=Bob").bodyAsText())
        assertEquals("User not found", client.get("/hello?name=Nobody").bodyAsText())
    }
}
