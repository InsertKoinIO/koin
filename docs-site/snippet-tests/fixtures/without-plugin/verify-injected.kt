import org.koin.test.verify.MissingKoinDefinitionException
import org.koin.test.verify.verify
import kotlin.test.assertFailsWith

class UserId
class UserRepository

fun check() {
    checkKoinModule()
    assertFailsWith<MissingKoinDefinitionException> { presenterModule.verify() }
}
