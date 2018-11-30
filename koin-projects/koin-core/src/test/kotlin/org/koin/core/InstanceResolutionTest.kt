package org.koin.core

import org.junit.Assert.*
import org.junit.Test
import org.koin.Simple
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class InstanceResolutionTest {

    @Test
    fun `can resolve a single`() {

        val app = koinApplication {
            modules(
                module {
                    single { Simple.ComponentA() }
                })
        }

        val koin = app.koin
        val a: Simple.ComponentA = koin.get()
        val a2: Simple.ComponentA = koin.get()

        assertEquals(a, a2)
    }

    @Test
    fun `can lazy resolve a single`() {

        val app = koinApplication {
            modules(
                module {
                    single { Simple.ComponentA() }
                })
        }

        val koin = app.koin
        val a: Simple.ComponentA by koin.inject()
        val a2: Simple.ComponentA = koin.get()

        assertEquals(a, a2)
    }

    @Test
    fun `can resolve a singles by name`() {

        val app = koinApplication {
            modules(
                module {
                    val componentA = Simple.ComponentA()
                    single("A") { componentA }
                    single("B") { componentA }
                })
        }

        val koin = app.koin
        val a: Simple.ComponentA = koin.get(name = "A")
        val b: Simple.ComponentA = koin.get(name = "B")

        assertEquals(a, b)
    }

    @Test
    fun `can resolve a factory by name`() {

        val app = koinApplication {
            modules(
                module {
                    val componentA = Simple.ComponentA()
                    factory("A") { componentA }
                    factory("B") { componentA }
                })
        }

        val koin = app.koin
        val a: Simple.ComponentA = koin.get(name = "A")
        val b: Simple.ComponentA = koin.get(name = "B")

        assertEquals(a, b)
    }

    @Test
    fun `can resolve a factory`() {

        val app = koinApplication {
            modules(
                module {
                    factory { Simple.ComponentA() }
                })
        }

        val koin = app.koin
        val a: Simple.ComponentA = koin.get()
        val a2: Simple.ComponentA = koin.get()

        assertNotEquals(a, a2)
    }

    @Test
    fun `should resolve default`() {

        val app = koinApplication {
            modules(
                module {
                    single<Simple.ComponentInterface1>("2") { Simple.Component2() }
                    single<Simple.ComponentInterface1> { Simple.Component1() }
                })
        }

        val koin = app.koin
        val component: Simple.ComponentInterface1 = koin.get()

        assertTrue(component is Simple.Component1)
        assertTrue(koin.get<Simple.ComponentInterface1>("2") is Simple.Component2)
    }
}