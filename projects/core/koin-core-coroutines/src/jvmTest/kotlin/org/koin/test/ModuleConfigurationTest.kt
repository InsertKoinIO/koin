package org.koin.test

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.awaitAllStartJobs
import org.koin.core.logger.Level
import org.koin.core.module.moduleConfiguration
import org.koin.dsl.koinApplication
import org.koin.dsl.lazyModule
import org.koin.dsl.module

class Simple {
    class ComponentA
    class ComponentB(val a: ComponentA)
    class ComponentC(val a: ComponentA)
}

class ModuleConfigurationTest {

    @OptIn(KoinInternalApi::class)
    @Test
    fun verify_module_configuration() {

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

        assert(conf._modules.size == 1)
        assert(conf._lazyModules.size == 1)

    }

    @OptIn(KoinInternalApi::class)
    @Test
    fun verify_koin_app_module_configuration() = runBlocking<Unit> {

        val m1 = module {
            single { Simple.ComponentA() }
        }
        val lm1 = lazyModule {
            single { Simple.ComponentB(get()) }
        }

        val koin = koinApplication {
            printLogger(Level.DEBUG)
            moduleConfiguration {
                modules(m1)
                lazyModules(lm1)
//                dispatcher(Dispatchers.IO) //not required
            }
        }.koin

        koin.awaitAllStartJobs()

        assert(koin.instanceRegistry.instances.size == 2)
    }

    @OptIn(KoinInternalApi::class, KoinExperimentalAPI::class)
    @Test
    fun verify_koin_ext_app_module_configuration() = runBlocking<Unit> {

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

        val koin = koinApplication {
            moduleConfiguration(conf)
        }.koin

        koin.awaitAllStartJobs()
        assert(koin.instanceRegistry.instances.size == 2)
    }

}