package org.koin.test

import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.awaitAllStartJobs
import org.koin.core.coroutine.KoinCoroutinesEngine
import org.koin.core.extension.coroutinesEngine
import org.koin.core.lazyModules
import org.koin.dsl.koinApplication
import org.koin.dsl.lazyModule
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(KoinInternalApi::class)
class ExtensionTest {

    @Test
    fun setup_extension() {
        val koin = koinApplication {
            coroutinesEngine()
        }.koin

        assert(koin.extensionManager.getExtensionOrNull<KoinCoroutinesEngine>(KoinCoroutinesEngine.EXTENSION_NAME) != null)
    }

    @Test
    fun lazy_load() {
        val koin = koinApplication {
            lazyModules(lazyModule {  })
        }.koin

        val coroutinesEngine =
            koin.extensionManager.getExtension<KoinCoroutinesEngine>(KoinCoroutinesEngine.EXTENSION_NAME)
        assertEquals(1,coroutinesEngine.startJobs.size)
    }

    @Test
    fun wait_jobs() = runBlocking<Unit> {
        val koin = koinApplication {
            lazyModules(lazyModule {  })
        }.koin

        val coroutinesEngine = koin.extensionManager.getExtension<KoinCoroutinesEngine>(KoinCoroutinesEngine.EXTENSION_NAME)
        assert(coroutinesEngine.startJobs.size == 1)
        val job = coroutinesEngine.startJobs[0]
        assert(job.isActive)
        koin.awaitAllStartJobs()

        assert(coroutinesEngine.startJobs.isEmpty())
    }

    @Test
    fun close_extension() {
        val koin = koinApplication {
            coroutinesEngine()
        }.koin

        koin.close()
        val coroutinesEngine =
            koin.extensionManager.getExtension<KoinCoroutinesEngine>(KoinCoroutinesEngine.EXTENSION_NAME)
        assertFalse(coroutinesEngine.coroutineContext.isActive)
    }
}
