package org.koin.core

import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

/**
 * Regression test for the override-leak bug: when `declare` overrides a binding,
 * the original `InstanceFactory` is left orphaned in `Module.mappings` with its
 * cached singleton intact. After `stopKoin()`, only the declare-installed factory
 * is reachable from `_instances` and so only that one is dropped; the orphan
 * survives. The next `startKoin { modules(sameModuleInstance) }` re-registers
 * the orphan with its stale cache.
 *
 * Without the fix in `InstanceRegistry.saveMapping` (drop the displaced factory
 * on override), this test fails: the second cycle returns the same instance
 * from cycle 1 with its mutation still present.
 */
class DeclareOverrideDropsCachedSingleTest {

    private class Counter {
        var value: Int = 0
    }

    @AfterTest
    fun stop() {
        runCatching { stopKoin() }
    }

    @Test
    fun `declare override drops the displaced factory's cached singleton across stopKoin`() {
        val sharedModule = module {
            single { Counter() }
        }

        // Cycle 1: resolve & mutate the module's singleton, then declare an override.
        startKoin { modules(sharedModule) }
        val first = KoinPlatform.getKoin().get<Counter>()
        first.value = 999

        // declare swaps the registry mapping but leaves the original factory in
        // sharedModule.mappings holding `first` as its cached value.
        KoinPlatform.getKoin().declare<Counter>(Counter())

        stopKoin()

        // Cycle 2: reload the same Module instance.
        startKoin { modules(sharedModule) }
        val second = KoinPlatform.getKoin().get<Counter>()

        assertNotSame(first, second, "stopKoin must reset the module-owned singleton even after a `declare` override")
        assertEquals(0, second.value, "fresh singleton should not carry mutated state from the previous Koin lifecycle")
    }
}
