package org.koin.ktor.ext

import io.ktor.server.application.*
import io.ktor.server.testing.*
import org.koin.core.annotation.KoinReflectAPI
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.ktor.plugin.KoinIsolated
import org.koin.mp.KoinPlatform
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * @author vinicius
 * @author Victor Alenkov
 *
 */
class Foo(val name: String = "")
class Bar(val name: String = "")
class Bar2(val name: String = "")

@OptIn(KoinReflectAPI::class)
class KoinFeatureTest {

    @AfterTest
    fun after(){
        stopKoin()
    }

    @Test
    fun `can install feature`() {
        val module = module {
            single { Foo("bar") }
        }
        withApplication {
            application.install(Koin) {
                modules(module)
            }
            val bean = KoinPlatform.getKoin().getOrNull<Foo>()
            assertNotNull(bean)
        }
    }

    @Test
    fun `can install feature - isolated context`() {
        val module = module {
            single { Foo("bar") }
        }
        withApplication {
            application.install(KoinIsolated) {
                modules(module)
            }
            val bean1 = application.get<Foo>()
            assertNotNull(bean1)
            val bean2 = runCatching { KoinPlatform.getKoin().getOrNull<Foo>() }.getOrNull()
            assertNull(bean2)
        }
    }

//    @Test
//    fun `Using the koinModules extension`() {
//        withApplication {
//            assertNull(GlobalContext.getOrNull())
//            assertNull(application.featureOrNull(Koin))
//
//            application.modules(
//                module {
//                    single<Foo>()
//                },
//                module {
//                    single<Bar>()
//                }
//            )
//            assertNotNull(application.getKoin().getOrNull<Foo>())
//            assertNotNull(application.getKoin().getOrNull<Bar>())
//        }
//    }
//
//    @Test
//    fun `Using the koin extension (with pre-installation of the module)`() {
//        withApplication {
//            assertNull(GlobalContext.getOrNull())
//            assertNull(application.featureOrNull(Koin))
//
//            application.install(Koin)
//            assertNotNull(GlobalContext.getOrNull())
//            assertNotNull(application.featureOrNull(Koin))
//            assertNull(application.getKoin().getOrNull<Foo>())
//
//            application.koin {
//                loadKoinModules(module {
//                    single<Foo>()
//                })
//            }
//            assertNotNull(application.getKoin().getOrNull<Foo>())
//        }
//    }
//
//    @Test
//    @Ignore
//    fun `Create required beans`() {
//        withApplication {
//            var s = "zero"
//
//            application.koin {
//                modules(module {
//                    single {
//                        s = "two"
//                        newInstance<Foo>(emptyParametersHolder())
//                    }
//                })
//            }
//            assertEquals("zero", s)
//
//            application.koin {
//                modules(module {
//                    single(createdAtStart = true) {
//                        s = "one"
//                        newInstance<Bar>(emptyParametersHolder())
//                    }
//                    single {
//                        s = "three"
//                        newInstance<Bar2>(emptyParametersHolder())
//                    }
//                })
//                createEagerInstances()
//            }
//
//            assertEquals("one", s)
//
//            assertNotNull(application.getKoin().getOrNull<Foo>())
//            assertEquals("two", s)
//
//            assertNotNull(application.getKoin().getOrNull<Bar>())
//            assertEquals("two", s)
//
//            assertNotNull(application.getKoin().getOrNull<Bar2>())
//            assertEquals("three", s)
//        }
//    }
//
//    @Suppress("EXPERIMENTAL_API_USAGE_FUTURE_ERROR")
//    @Test
//    fun `custom stop listeners`() {
//        val module = module {
//            single { Foo("bar") }
//        }
//        var c = 0
//        withTestApplication {
//            assertEquals(0, c)
//
//            val monitor = environment.monitor
//            monitor.subscribe(KoinApplicationStopPreparing) {
//                c += 1
//            }
//            monitor.subscribe(KoinApplicationStopped) {
//                c += 2
//            }
//
//            application.install(Koin) {
//                modules(module)
//            }
//            val bean = GlobalContext.get().get<Foo>()
//            assertNotNull(bean)
//            assertEquals(0, c)
//        }
//        assertEquals(3, c)
//
//        withTestApplication {
//            assertEquals(3, c)
//            application.install(Koin) {
//                modules(module)
//                environment.monitor.subscribe(KoinApplicationStarted) {
//                    c = 0
//                }
//                environment.monitor.subscribe(KoinApplicationStopped) {
//                    c += 4
//                }
//            }
//            val bean = GlobalContext.get().get<Foo>()
//            assertNotNull(bean)
//            assertEquals(0, c)
//        }
//        assertEquals(4, c)
//    }
}
