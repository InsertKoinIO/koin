package org.koin.core

import org.junit.Test
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import java.util.*
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

    @Test
    fun `should resolve different scoped declared definitions`() {
        val koin: Koin = koinApplication {
            modules(module)
        }.koin

        // Create two scopes
        val scopeA = koin.createScope<MyScope>(UUID.randomUUID().toString(), MyScope())
        val scopeB = koin.createScope<MyScope>(UUID.randomUUID().toString(), MyScope())

        // Declare scope data on each scope
        val value_A = "A"
        scopeA.declare(MyScopedData(value_A))
        val value_B = "B"
        scopeB.declare(MyScopedData(value_B))

        // Get scope of each data
        assertNotEquals(scopeA.get<MyScopedData>().name, scopeB.get<MyScopedData>().name)
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
