package org.koin.experimental.builder

import org.junit.Assert.*
import org.junit.Test
import org.koin.core.error.InstanceCreationException
import org.koin.core.logger.Level
import org.koin.core.time.measureDurationOnly
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.KoinTest

class CreateAPITest : KoinTest {

    @Test
    fun `should find 1st constructor and build`() {
        val koin = koinApplication {
            useLogger(Level.DEBUG)
            loadModules(module {
                single { ComponentA() }
                single { ComponentB(get()) }
            })
        }.koin

        val duration = measureDurationOnly {
            koin.get<ComponentB>()
        }
        println("create api in $duration ms")

        val createKoin = koinApplication {
            useLogger(Level.DEBUG)
            loadModules(module {
                single { ComponentA() }
                single { create<ComponentB>() }
            })
        }.koin

        val createDuration = measureDurationOnly {
            createKoin.get<ComponentB>()
        }
        println("create api in $createDuration ms")
    }

    @Test
    fun `create with missing dependency`() {
        try {
            val koin = koinApplication {
                useLogger(Level.DEBUG)
                loadModules(module {
                    single { create<ComponentB>() }
                })
            }.koin

            koin.get<ComponentB>()
            fail("should not get ComponentB")
        } catch (e: InstanceCreationException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `create with empty ctor`() {
        val koin = koinApplication {
            useLogger(Level.DEBUG)
            loadModules(module {
                single { create<ComponentA>() }
            })
        }.koin

        koin.get<ComponentA>()
    }

    @Test
    fun `create for interface`() {
        val koin = koinApplication {
            useLogger(Level.DEBUG)
            loadModules(module {
                single { ComponentA() }
                single<Component> { create<ComponentD>() }
            })
        }.koin

        koin.get<Component>()
    }

    @Test
    fun `create factory for interface`() {
        val koin = koinApplication {
            useLogger(Level.DEBUG)
            loadModules(module {
                single { ComponentA() }
                factory<Component> { create<ComponentD>() }
            })
        }.koin

        val f1 = koin.get<Component>()
        val f2 = koin.get<Component>()
        assertNotEquals(f1, f2)
        assertEquals(f1.a, f2.a)
    }

    @Test
    fun `create API overhead`() {
        val koin = koinApplication {
            useLogger(Level.DEBUG)
            loadModules(module {
                single { ComponentA() }
                factory<Component> { create<ComponentD>() }
            })
        }.koin

        (1..3).forEach {
            koin.get<Component>()
        }
    }
}