package org.koin.core

import kotlin.test.Test
import org.koin.Simple
import org.koin.core.logger.Level
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.assertEquals

class LazyInstanceResolution {
    @Test
    fun `can lazy resolve a single`() {

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
    fun `create  eager`() {
        var i = 0
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module(createdAtStart = true) {
                    single { i++; Simple.ComponentA() }
                })
        }

        val koin = app.koin
        assertEquals(0,i)
        koin.createEagerInstances()
        assertEquals(1,i)
        koin.get<Simple.ComponentA>()
        assertEquals(1,i)
    }

    @Test
    fun `create  eager twice`() {
        var i = 0
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module(createdAtStart = true) {
                    single { i++; Simple.ComponentA() }
                })
        }

        val koin = app.koin
        assertEquals(0,i)
        koin.createEagerInstances()
        assertEquals(1,i)
        koin.createEagerInstances()
        assertEquals(1,i)
    }

    @Test
    fun `create  eager definitions`() {
        var i = 0
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module(createdAtStart = true) {
                    single { i++; Simple.ComponentA() }
                    single { i++; Simple.ComponentB(get()) }
                })
            createEagerInstances()
        }

        val koin = app.koin
        assertEquals(2,i)
        koin.get<Simple.ComponentA>()
        koin.get<Simple.ComponentB>()
        assertEquals(2,i)
    }

    @Test
    fun `create  eager definitions - one create`() {
        var i = 0
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single(createdAtStart = true) { i++; Simple.ComponentA() }
                    single { i++; Simple.ComponentB(get()) }
                })
            createEagerInstances()
        }

        val koin = app.koin
        assertEquals(1,i)
        koin.get<Simple.ComponentA>()
        assertEquals(1,i)
        koin.get<Simple.ComponentB>()
        assertEquals(2,i)
    }

    @Test
    fun `create  eager definitions different modules`() {
        var i = 0
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module(createdAtStart = true) {
                    single { i++; Simple.ComponentA() }
                },
                module(createdAtStart = true) {
                    single { i++; Simple.ComponentB(get()) }
                })
            createEagerInstances()
        }

        val koin = app.koin
        assertEquals(2,i)
        koin.get<Simple.ComponentA>()
        koin.get<Simple.ComponentB>()
        assertEquals(2,i)
    }

    @Test
    fun `create  eager definitions different modules - by default`() {
        var i = 0
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module(createdAtStart = true) {
                    single { i++; Simple.ComponentA() }
                },
                module(createdAtStart = true) {
                    single { i++; Simple.ComponentB(get()) }
                })
        }

        val koin = app.koin
        assertEquals(2,i)
        koin.get<Simple.ComponentA>()
        koin.get<Simple.ComponentB>()
        assertEquals(2,i)
    }

    @Test
    fun `create eager definitions different modules - one eager`() {
        var i = 0
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { i++; Simple.ComponentA() }
                },
                module {
                    single(createdAtStart = true) { i++; Simple.ComponentB(get()) }
                })
            createEagerInstances()
        }

        val koin = app.koin
        assertEquals(2,i)
        koin.get<Simple.ComponentA>()
        koin.get<Simple.ComponentB>()
        assertEquals(2,i)
    }

    @Test
    fun `eager definitions module`() {
        val module = module(createdAtStart = true) {
            single { Simple.ComponentA() }
            factory { Simple.ComponentB(get()) }
            scope<Simple.ComponentB> {
                scoped { Simple.ComponentC(get()) }
            }
        }

        assertEquals(1,module.eagerInstances.size)
    }
}