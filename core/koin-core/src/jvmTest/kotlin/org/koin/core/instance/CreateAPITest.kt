package org.koin.core.instance

import org.junit.Assert.*
import org.junit.Test
import org.koin.core.error.InstanceCreationException
import org.koin.core.logger.Level
import org.koin.core.time.measureDuration
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class CreateAPITest {

    @Test
    fun `should find 1st constructor and build`() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(module {
                single { ComponentA() }
                single { ComponentB(get()) }
            })
        }.koin

        val duration = measureDuration {
            koin.get<ComponentB>()
        }
        println("create api in $duration ms")

        val createKoin = koinApplication {
            printLogger(Level.DEBUG)
            modules(module {
                single { ComponentA() }
                single { newInstance(ComponentB::class) }
            })
        }.koin

        val createDuration = measureDuration {
            createKoin.get<ComponentB>()
        }
        println("create api in $createDuration ms")
    }

    @Test
    fun `create with missing dependency`() {
        try {
            val koin = koinApplication {
                printLogger(Level.DEBUG)
                modules(module {
                    single { newInstance(ComponentB::class) }
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
            printLogger(Level.DEBUG)
            modules(module {
                single { newInstance(ComponentA::class) }
            })
        }.koin

        koin.get<ComponentA>()
    }

    @Test
    fun `create for interface`() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(module {
                single { ComponentA() }
                single<Component> { newInstance(ComponentD::class) }
            })
        }.koin

        koin.get<Component>()
    }

    @Test
    fun `create factory for interface`() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(module {
                single { ComponentA() }
                factory<Component> { newInstance(ComponentD::class) }
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
            printLogger(Level.DEBUG)
            modules(module {
                single { newInstance<ComponentA>() }
                factory<Component> { newInstance<ComponentD>() }
            })
        }.koin

        (1..3).forEach {
            koin.get<Component>()
        }
    }
}