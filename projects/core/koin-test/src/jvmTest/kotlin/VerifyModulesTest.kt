import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.test.Test
import kotlin.test.fail
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.test.Simple
import org.koin.test.verify.*

class VerifyModulesTest {

    @Test
    fun verify_one_simple_module() {
        val module = module {
            single { Simple.ComponentA() }
            single { Simple.ComponentB(get()) }
        }

        try {
            module.verify()
        } catch (e: MissingKoinDefinitionException) {
            fail("Should not fail to verify module - $e")
        }
    }

    @Test
    fun verify_one_simple_broken_module() {
        val module = module {
            single { Simple.ComponentB(get()) }
        }

        try {
            module.verify()
            fail("Should fail to verify module")
        } catch (e: MissingKoinDefinitionException) {
            System.err.println("$e")
        }
    }

    @Test
    fun allow_verify_optional_dep() {
        val module = module {
            single { Simple.ComponentBOptional(get()) }
        }

        // verified as optional
        module.verify()
    }

    @Test
    fun allow_verify_optional_dep_ok() {
        val module = module {
            single { Simple.ComponentA() }
            single { Simple.ComponentBOptional(get()) }
        }

        module.verify()
    }

    @Test
    fun verify_one_simple_module_w_interface() {
        val module = module {
            single { Simple.ComponentA() }
            single { Simple.ComponentB(get()) } bind Simple.MyComponentB::class
        }

        try {
            module.verify()
        } catch (e: MissingKoinDefinitionException) {
            fail("Should not fail to verify module - $e")
        }
    }

    @Test
    fun verify_one_simple_module_w_bound_interface() {
        val module = module {
            single { Simple.ComponentA() } bind Simple.MyComponentA::class
            single { Simple.ComponentB(get()) } bind Simple.MyComponentB::class
            single { Simple.ComponentC(get()) }
        }

        try {
            module.verify()
        } catch (e: MissingKoinDefinitionException) {
            fail("Should not fail to verify module - $e")
        }
    }

    @Test
    fun verify_one_simple_module_w_broken_bound_interface() {
        val module = module {
            single { Simple.ComponentA() } bind Simple.MyComponentA::class
            single { Simple.ComponentB(get()) }
            single { Simple.ComponentC(get()) }
        }

        try {
            module.verify()
            fail("Should not fail to verify module")
        } catch (e: MissingKoinDefinitionException) {
            System.err.println("$e")
        }
    }

    @Test
    fun verify_one_simple_module_w_submodule() {
        val module = module {
            includes(
                module {
                    single { Simple.ComponentA() }
                },
            )
            single { Simple.ComponentB(get()) } bind Simple.MyComponentB::class
        }

        try {
            module.verify()
        } catch (e: MissingKoinDefinitionException) {
            fail("Should not fail to verify module - $e")
        }
    }

    @Test
    fun verify_one_simple_module_w_extra() {
        val module = module {
            single { (a: Simple.ComponentA) -> Simple.ComponentB(a) }
        }

        try {
            module.verify(extraTypes = listOf(Simple.ComponentA::class))
        } catch (e: MissingKoinDefinitionException) {
            fail("Should not fail to verify module - $e")
        }
    }

    @Test
    fun verify_one_simple_module_w_verify_extra() {
        Verify.addExtraTypes(Simple.ComponentA::class)

        val module = module {
            single { (a: Simple.ComponentA) -> Simple.ComponentB(a) }
        }

        try {
            module.verify()
        } catch (e: MissingKoinDefinitionException) {
            fail("Should not fail to verify module - $e")
        }

        Verify.whiteList.clear()
        Verify.addExtraTypes(Verify.primitiveTypes)
    }

    @Test
    fun verify_one_simple_module_w_extra_broken() {
        val module = module {
            single { (a: Simple.ComponentA) -> Simple.ComponentB(a) }
        }

        try {
            module.verify()
            fail("Should not fail to verify module")
        } catch (e: MissingKoinDefinitionException) {
            System.err.println("$e")
        }
    }

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun verify_one_simple_module_w_inject_param() {
        val module = module {
            single { (a: Simple.ComponentA) -> Simple.ComponentB(a) }
        }

        module.verify(
            injections = injectedParameters(
                definition<Simple.ComponentB>(Simple.ComponentA::class)
            )
        )
    }

    @Test
    fun verify_cycle_deps() {
        val module = module {
            singleOf(Simple::CycleAB)
            singleOf(Simple::CycleBA)
        }

        try {
            module.verify()
            fail("Should not fail to verify module")
        } catch (e: CircularInjectionException) {
            System.err.println("$e")
        }
    }

    @Test
    fun `verify dependency and param in one class - 3`() {
        val modules = module {
            factory { p -> Simple.MyComplexBool(get(), p.get()) }
        }

        modules.verify(extraTypes = listOf(Simple.ComponentA::class, Boolean::class))
    }

    @Test
    fun `verify function builder`() {
        val modules = module {
            factoryOf(Simple::buildB)
        }

        modules.verify(extraTypes = listOf(Simple.ComponentA::class))
    }

    @Test
    fun `verify list`(){
        module {
            single { Simple.ComponentA() }
            single { Simple.ComponentBList(getAll()) }
        }.verify()
    }

    @Test
    fun `verify lazy`(){
        module {
            single { Simple.ComponentA() }
            single { Simple.ComponentBLazy(inject()) }
        }.verify()
    }

    @Test
    fun `verify annotated param`(){
        module {
            single { (a : Others.ComponentA) -> Others.ComponentBParam(a) }
        }.verify()
    }

    @Test
    fun `verify annotated provided`(){
        module {
            single { (a : Others.ComponentA) -> Others.ComponentBProvided(a) }
        }.verify()
    }

    @Test
    fun `verify annotated provided - whitelisted`(){
        module {
            single { (a : Others.ComponentA) -> Others.ComponentBProvided(a) }
            single { (a : Others.ComponentA) -> Others.ComponentBProvided2(a) }
        }.verify()
    }

    @Test
    fun `verify annotated param - fail`(){
        try {
            module {
                single { (a : Others.ComponentA) -> Others.ComponentB(a) }
            }.verify()
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
