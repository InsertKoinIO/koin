package org.koin.ktor.ext

import io.ktor.application.featureOrNull
import io.ktor.application.install
import io.ktor.server.testing.withApplication
import io.ktor.server.testing.withTestApplication
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.experimental.builder.create
import org.koin.experimental.builder.single

/**
 * @author vinicius
 * @author Victor Alenkov
 *
 */
class Foo(val name: String = "")

class Bar(val name: String = "")
class Bar2(val name: String = "")

class KoinFeatureTest {

    @Test
    fun `can install feature`() {
        val module = module {
            single { Foo("bar") }
        }
        withApplication {
            application.install(Koin) {
                modules(module)
            }
            val bean = GlobalContext.get().get<Foo>()
            assertNotNull(bean)
        }
    }

    @Test
    fun `Koin does not contain modules`() {
        withApplication {
            assertNull(GlobalContext.getOrNull())
            assertNull(application.featureOrNull(Koin))

            application.install(Koin)
            assertNotNull(application.featureOrNull(Koin))
            val koin = GlobalContext.getOrNull()
            assertNotNull(koin)
            requireNotNull(koin)

            assertNull(koin.getOrNull<Foo>())
        }
    }

    @Test
    fun `add a Koin module to an already running application`() {
        withApplication {
            application.install(Koin)
            val koin = application.getKoin()

            assertNull(koin.getOrNull<Foo>())

            application.featureOrNull(Koin)?.let {
                loadKoinModules(module {
                    single<Foo>()
                })
            }
            assertNotNull(koin.getOrNull<Foo>())
        }
    }

    @Test
    fun `Using the koin extension`() {
        withApplication {
            assertNull(GlobalContext.getOrNull())
            assertNull(application.featureOrNull(Koin))

            application.koin {
                modules(module {
                    single<Foo>()
                })
            }
            assertNotNull(application.getKoin().getOrNull<Foo>())
        }
    }

    @Test
    fun `Using the koinModules extension`() {
        withApplication {
            assertNull(GlobalContext.getOrNull())
            assertNull(application.featureOrNull(Koin))

            application.modules(
                module {
                    single<Foo>()
                },
                module {
                    single<Bar>()
                }
            )
            assertNotNull(application.getKoin().getOrNull<Foo>())
            assertNotNull(application.getKoin().getOrNull<Bar>())
        }
    }

    @Test
    fun `Using the koin extension (with pre-installation of the module)`() {
        withApplication {
            assertNull(GlobalContext.getOrNull())
            assertNull(application.featureOrNull(Koin))

            application.install(Koin)
            assertNotNull(GlobalContext.getOrNull())
            assertNotNull(application.featureOrNull(Koin))
            assertNull(application.getKoin().getOrNull<Foo>())

            application.koin {
                loadKoinModules(module {
                    single<Foo>()
                })
            }
            assertNotNull(application.getKoin().getOrNull<Foo>())
        }
    }

    @Test
    fun `Create required beans`() {
        withApplication {
            var s = "zero"

            application.koin {
                modules(module {
                    single {
                        s = "two"
                        create<Foo>()
                    }
                })
            }
            assertEquals("zero", s)

            application.koin {
                modules(module {
                    single(createdAtStart = true) {
                        s = "one"
                        create<Bar>()
                    }
                    single {
                        s = "three"
                        create<Bar2>()
                    }
                })
            }

            assertEquals("one", s)

            assertNotNull(application.getKoin().getOrNull<Foo>())
            assertEquals("two", s)

            assertNotNull(application.getKoin().getOrNull<Bar>())
            assertEquals("two", s)

            assertNotNull(application.getKoin().getOrNull<Bar2>())
            assertEquals("three", s)
        }
    }

    @Test
    fun `custom stop listeners`() {
        val module = module {
            single { Foo("bar") }
        }
        var c = 0
        withTestApplication {
            assertEquals(0, c)

            val monitor = environment.monitor
            monitor.subscribe(KoinApplicationStopPreparing) {
                c += 1
            }
            monitor.subscribe(KoinApplicationStopped) {
                c += 2
            }

            application.install(Koin) {
                modules(module)
            }
            val bean = GlobalContext.get().get<Foo>()
            assertNotNull(bean)
            assertEquals(0, c)
        }
        Assert.assertEquals(3, c)

        withTestApplication {
            assertEquals(3, c)
            application.install(Koin) {
                modules(module)
                environment.monitor.subscribe(KoinApplicationStarted) {
                    c = 0
                }
                environment.monitor.subscribe(KoinApplicationStopped) {
                    c += 4
                }
            }
            val bean = GlobalContext.get().get<Foo>()
            assertNotNull(bean)
            assertEquals(0, c)
        }
        assertEquals(4, c)
    }
}
