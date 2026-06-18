import org.junit.After
import org.koin.core.context.stopKoin
import kotlin.test.Test
import kotlin.test.fail
import org.koin.core.error.InstanceCreationException
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.test.Simple
import org.koin.test.check.checkKoinModules
import org.koin.test.verify.MissingKoinDefinitionException

class CheckModulesTest {

    class ComponentWithNamedDependency(env: String) {
        init {
            require(env.isNotEmpty()) { "env was empty!" }
        }
    }

    /**
     * #2406 - checkModules must keep the named String single. It used to hand the
     * get(named("env")) MockParameter's default "", tripping the require() below.
     */
    @Test
    fun verify_module_with_named_string_dependency() {
        val module = module {
            single(named("env")) { "TEST" }
            single { ComponentWithNamedDependency(get(named("env"))) }
        }

        checkKoinModules(listOf(module))
    }

    @Test
    fun verify_one_simple_module() {
        val module = module {
            single { Simple.ComponentA() }
            single { Simple.ComponentB(get()) }
        }

        try {
            checkKoinModules(listOf(module))
        } catch (e: MissingKoinDefinitionException) {
            fail("Should not fail to verify module - $e")
        }
    }

    @Test(expected = InstanceCreationException::class)
    fun verify_one_simple_broken_module() {
        val module = module {
            single { Simple.ComponentB(get()) }
        }

        checkKoinModules(listOf(module))
        fail("Should fail to verify module")
    }

    @After
    fun after(){
        stopKoin()
    }
}
