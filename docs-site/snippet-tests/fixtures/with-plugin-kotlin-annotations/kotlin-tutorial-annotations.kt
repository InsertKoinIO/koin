// package: org.koin.sample
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertContains
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

@Singleton
class UserApplication(private val userService: UserService) {
    init {
        userService.loadUsers()
    }

    fun sayHello(name: String) {
        println(userService.prepareHelloMessage(userService.getUserOrNull(name)))
    }
}

// The tutorial prints its result, so capture it.
fun check() {
    val original = System.out
    val captured = ByteArrayOutputStream()
    System.setOut(PrintStream(captured))
    try {
        main()
    } finally {
        System.setOut(original)
    }
    assertContains(captured.toString(), "Hello 'Alice' (alice@example.com)!")
}
