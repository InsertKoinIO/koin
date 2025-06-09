package org.koin.test

import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.includes
import org.koin.core.module.moduleConfiguration
import org.koin.dsl.lazyModule
import org.koin.dsl.module
import org.koin.test.verify.verify
import kotlin.test.Test


@OptIn(KoinExperimentalAPI::class)
class VerifyModuleConfigurationTest {

    @Test
    fun verify_module_configuration_list() {

        val m1 = module {
            single { Simple.ComponentA() }
        }
        val m2 = module {
            single { Simple.ComponentB(get()) }
        }
        val conf = moduleConfiguration {
            modules(m1, m2)
        }

        conf.verify()
    }

    @Test
    fun verify_module_configuration_include() {

        val m2 = module {
            single { Simple.ComponentB(get()) }
        }
        val m1 = module {
            includes(m2)
            single { Simple.ComponentA() }
        }

        val conf = moduleConfiguration {
            modules(m1)
        }

        conf.verify()
    }

    @Test
    fun verify_module_configuration_linked_lazy() {

        val m1 = module {
            single { Simple.ComponentA() }
        }
        val lm1 = lazyModule {
            single { Simple.ComponentB(get()) }
        }
        val conf = moduleConfiguration {
            modules(m1)
            lazyModules(lm1)
        }

        conf.verify()
    }

    @Test
    fun verify_module_configuration_lazy_common() {

        val common = module {
            single { Simple.ComponentA() }
        }
        val m1 = module {
            includes(common)
            single { Simple.ComponentC(get()) }
        }
        val lm1 = lazyModule {
            includes(common)
            single { Simple.ComponentB(get()) }
        }
        val conf = moduleConfiguration {
            modules(m1)
            lazyModules(lm1)
        }

        conf.verify()
    }

    @Test
    fun verify_module_configuration_lazy_list() {

        val common = lazyModule {
            single { Simple.ComponentA() }
        }
        val lm1 = lazyModule {
            single { Simple.ComponentB(get()) }
        }
        val conf = moduleConfiguration {
            lazyModules(lm1,common)
        }

        conf.verify()
    }

    @Test
    fun verify_module_configuration_lazy_include() {

        val common = lazyModule {
            single { Simple.ComponentA() }
        }
        val lm1 = lazyModule {
            includes(common)
            single { Simple.ComponentB(get()) }
        }
        val conf = moduleConfiguration {
            lazyModules(lm1)
        }

        conf.verify()
    }

    @Test
    fun `verify index duplication`(){
        val m1 = module {
            single { Simple.ComponentA() }
        }
        val m2 = module {
            single { Simple.ComponentA() }
        }
        moduleConfiguration {
            modules(m1,m2)
        }.verify()

    }

}
