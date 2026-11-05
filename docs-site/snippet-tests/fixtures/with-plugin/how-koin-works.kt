import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.plugin.module.dsl.factory
import org.koin.plugin.module.dsl.single

// The classes of the Quickstart, which this page reuses.
class UserRepository {
    fun findName() = "Alice"
}

class UserService(private val repository: UserRepository) {
    fun greet() = "Hello, ${repository.findName()}!"
}

class UserApplication(private val service: UserService) {
    fun run() = println(service.greet())
}

private var singlesCreated = 0

class CountedSingle {
    init { singlesCreated++ }
}

class NewEachTime

fun check() {
    main()

    // "single keeps one instance, created on its first request", "factory creates a new instance on every request".
    val koin = koinApplication {
        modules(module {
            single<CountedSingle>()
            factory<NewEachTime>()
        })
    }.koin
    assertEquals(0, singlesCreated)
    assertSame(koin.get<CountedSingle>(), koin.get<CountedSingle>())
    assertEquals(1, singlesCreated)
    assertNotSame(koin.get<NewEachTime>(), koin.get<NewEachTime>())
}
