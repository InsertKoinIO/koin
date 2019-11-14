package org.koin.core

import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.Simple
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.assertDefinitionsCount

class MultipleModuleDeclarationTest {

    @Test
    fun `run with DI with several modules`() {

        val app = koinApplication {
            modules(listOf(
                module {
                    single { Simple.ComponentA() }
                },
                module {
                    single { Simple.ComponentB(get()) }
                }))
        }

        app.assertDefinitionsCount(2)
    }

    @Test
    fun `resolve DI with several modules`() {

        val app = koinApplication {
            modules(listOf(
                module {
                    single { Simple.ComponentA() }
                },
                module {
                    single { Simple.ComponentB(get()) }
                }))
        }

        val koin = app.koin
        val a = koin.get<Simple.ComponentA>()
        val b = koin.get<Simple.ComponentB>()

        assertEquals(a, b.a)
    }
}