package org.koin.test

import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.context.unloadKoinModules
import org.koin.core.error.NoDefinitionFoundException
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.lazyModule
import org.koin.mp.KoinPlatformTools
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UnloadTarget

class LoadLazyModulesTest {

    @Test
    fun test_unload_realized_lazy_module() {
        val lazyMod = lazyModule {
            singleOf(::UnloadTarget)
        }
        startKoin {
            loadKoinModules(lazyMod)
        }

        assertNotNull(KoinPlatformTools.defaultContext().get().get<UnloadTarget>())

        unloadKoinModules(lazyMod)

        assertFailsWith<NoDefinitionFoundException> {
            KoinPlatformTools.defaultContext().get().get<UnloadTarget>()
        }

        stopKoin()
    }

    @Test
    fun test_unload_unrealized_lazy_module_does_not_initialize_it() {
        var initialized = false
        val lazyMod = lazyModule {
            initialized = true
            singleOf(::UnloadTarget)
        }
        startKoin { }

        unloadKoinModules(lazyMod)

        assertTrue(!initialized, "unloading a never-realized LazyModule must not trigger its initializer")

        stopKoin()
    }
}
