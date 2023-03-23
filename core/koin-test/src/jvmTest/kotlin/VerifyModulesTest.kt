import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.test.Simple
import org.koin.test.verify.CircularInjectionException
import org.koin.test.verify.MissingKoinDefinitionException
import org.koin.test.verify.Verify
import org.koin.test.verify.verify
import kotlin.test.Test
import kotlin.test.fail

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
            includes(module {
                single { Simple.ComponentA() }
            })
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
            single { (a : Simple.ComponentA) -> Simple.ComponentB(a) }
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
            single { (a : Simple.ComponentA) -> Simple.ComponentB(a) }
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
            single { (a : Simple.ComponentA) -> Simple.ComponentB(a) }
        }

        try {
            module.verify()
            fail("Should not fail to verify module")
        } catch (e: MissingKoinDefinitionException) {
            System.err.println("$e")
        }
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
}