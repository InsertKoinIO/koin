package org.koin.core

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.instance.ScopedInstanceFactory
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools
import org.koin.mp.generateId
import kotlin.collections.first
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class DeclareInstanceJVMTest {

    class MyScope

    class MyScopedData(var name: String)

    private val module = module {
        scope<MyScope> {
            scoped { MyScopedData("") }
        }
    }

    @OptIn(KoinInternalApi::class)
    @Test
    fun `should resolve different scoped declared definitions`() {
        val koin: Koin = koinApplication {
            modules(module)
        }.koin

        val factory = koin.instanceRegistry.instances.values.first() as ScopedInstanceFactory<*>

        // Create two scopes
        val scopeA = koin.createScope<MyScope>(KoinPlatformTools.generateId(), MyScope())
        val scopeB = koin.createScope<MyScope>(KoinPlatformTools.generateId(), MyScope())

        // Declare scope data on each scope
        val value_A = "A"
        scopeA.declare(MyScopedData(value_A))
        val value_B = "B"
        scopeB.declare(MyScopedData(value_B))

        assertEquals(2, factory.size())

        // Get scope of each data
        assertNotEquals(scopeA.get<MyScopedData>().name, scopeB.get<MyScopedData>().name)
        scopeA.close()
        scopeB.close()

        assertEquals(0, factory.size())

        // Create two scopes
        val scopeC = koin.createScope<MyScope>(KoinPlatformTools.generateId(), MyScope())
        val value_C = "C"
        scopeC.declare(MyScopedData(value_C))

        assertEquals(1, factory.size())

        scopeC.close()
        assertEquals(0, factory.size())
    }

    @Test
    fun `should resolve root declared definitions`() {
        val koin: Koin = koinApplication {
            modules(module)
        }.koin

        // Declare scope data on each scope
        val value_A = "A"
        koin.declare(MyScopedData(value_A))
        val value_B = "B"
        koin.declare(MyScopedData(value_B))

        // Get scope of each data
        assertEquals(koin.get<MyScopedData>().name, koin.get<MyScopedData>().name)
    }
}
