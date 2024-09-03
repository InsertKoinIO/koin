package org.koin.core.instance

import org.junit.Assert.*
import org.junit.Test
import org.koin.core.error.InstanceCreationException
import org.koin.core.logger.Level
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.dsl.single
import kotlin.time.measureTime

class CreateAPITest {

    @Test
    fun `should find 1st constructor and build`() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { ComponentA() }
                    single { ComponentB(get()) }
                },
            )
        }.koin

        val duration = measureTime {
            koin.get<ComponentB>()
        }
        println("create api in $duration")

        val createKoin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { ComponentA() }
                    single { newInstance(ComponentB::class, it) }
                },
            )
        }.koin

        val createDuration = measureTime {
            createKoin.get<ComponentB>()
        }
        println("create api in $createDuration")
    }

    @Test
    fun `create with missing dependency`() {
        try {
            val koin = koinApplication {
                printLogger(Level.DEBUG)
                modules(
                    module {
                        single { newInstance(ComponentB::class, it) }
                    },
                )
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
            modules(
                module {
                    single { newInstance(ComponentA::class, it) }
                },
            )
        }.koin

        koin.get<ComponentA>()
    }

    @Test
    fun `create for interface`() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { ComponentA() }
                    single<Component> { newInstance(ComponentD::class, it) }
                },
            )
        }.koin

        koin.get<Component>()
    }

    @Test
    fun `create factory for interface`() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { ComponentA() }
                    factory<Component> { newInstance(ComponentD::class, it) }
                },
            )
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
            modules(
                module {
                    single { newInstance<ComponentA>(it) }
                    factory<Component> { newInstance<ComponentD>(it) }
                },
            )
        }.koin

        (1..3).forEach {
            koin.get<Component>()
        }
    }

    @Test
    fun `create API with param`() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single<ComponentD>()
                },
            )
        }.koin

        koin.get<ComponentD> { parametersOf(ComponentA()) }
    }
}
