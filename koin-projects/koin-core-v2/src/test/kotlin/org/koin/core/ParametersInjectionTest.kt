package org.koin.core

import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test
import org.koin.Simple
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class ParametersInjectionTest {

    @Test
    fun `can create a single with parameters`() {

        val app = koinApplication {
            loadModules(
                module {
                    single { (i: Int) -> Simple.MySingle(i) }
                })
        }

        val koin = app.koin
        val a: Simple.MySingle = koin.get { parametersOf(42) }

        assertEquals(42, a.id)
    }

    @Test
    fun `can get a single created with parameters - no need of give it again`() {

        val app = koinApplication {
            loadModules(
                module {
                    single { (i: Int) -> Simple.MySingle(i) }
                })
        }

        val koin = app.koin
        val a: Simple.MySingle = koin.get { parametersOf(42) }

        assertEquals(42, a.id)

        val a2: Simple.MySingle = koin.get()

        assertEquals(42, a2.id)
    }

    @Test
    fun `can create factories with params`() {

        val app = koinApplication {
            loadModules(
                module {
                    factory { (i: Int) -> Simple.MyFactory(i) }
                })
        }

        val koin = app.koin
        val a: Simple.MyFactory = koin.get { parametersOf(42) }
        val a2: Simple.MyFactory = koin.get { parametersOf(43) }

        assertEquals(42, a.id)
        assertEquals(43, a2.id)
    }

    @Test
    @Ignore
    fun `chained definition injection`() {
        TODO()
    }
}