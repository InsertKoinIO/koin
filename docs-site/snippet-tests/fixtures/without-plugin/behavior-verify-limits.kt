import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.test.verify.MissingKoinDefinitionException
import org.koin.test.verify.verify
import kotlin.test.assertFailsWith

class Missing
interface Repository
class RepositoryImpl(val missing: Missing) : Repository
class WithString(val value: String)

// testing/verify: an interface type is not checked, basic types are whitelisted.
fun check() {
    module { single<Repository> { RepositoryImpl(get()) } }.verify()
    assertFailsWith<MissingKoinDefinitionException> {
        module { single { RepositoryImpl(get()) } bind Repository::class }.verify()
    }
    module { factory { (value: String) -> WithString(value) } }.verify()
}
