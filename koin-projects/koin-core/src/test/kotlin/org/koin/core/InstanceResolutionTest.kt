package org.koin.core

import org.junit.Assert.*
import org.junit.Test
import org.koin.Simple
import org.koin.core.qualifier.named
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
                        single(named("A")) { componentA }
                        single(named("B")) { componentA }
                    })
        }

        val koin = app.koin
        val a: Simple.ComponentA = koin.get(qualifier = named("A"))
        val b: Simple.ComponentA = koin.get(qualifier = named("B"))

        assertEquals(a, b)
    }

    @Test
    fun `can resolve a factory by name`() {

        val app = koinApplication {
            modules(
                    module {
                        val componentA = Simple.ComponentA()
                        factory(named("A")) { componentA }
                        factory(named("B")) { componentA }
                    })
        }

        val koin = app.koin
        val a: Simple.ComponentA = koin.get(qualifier = named("A"))
        val b: Simple.ComponentA = koin.get(qualifier = named("B"))

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
                        single<Simple.ComponentInterface1>(named("2")) { Simple.Component2() }
                        single<Simple.ComponentInterface1> { Simple.Component1() }
                    })
        }

        val koin = app.koin
        val component: Simple.ComponentInterface1 = koin.get()

        assertTrue(component is Simple.Component1)
        assertTrue(koin.get<Simple.ComponentInterface1>(named("2")) is Simple.Component2)
    }
}