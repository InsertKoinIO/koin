// package: com.example.app
import kotlin.test.assertTrue
import org.koin.core.annotation.KoinApplication
import org.koin.plugin.module.dsl.koinApplication

interface UserRepository {
    fun findName(): String
}

@KoinApplication(configurations = ["com.example.app"], modules = [AppModule::class])
class StyleApp

// "A class is bound to all its supertypes": UserRepository resolves with no explicit binding.
fun check() {
    val koin = koinApplication<StyleApp>().koin
    koin.get<UserApplication>()
    assertTrue(koin.get<UserRepository>() is UserRepositoryImpl)
}
