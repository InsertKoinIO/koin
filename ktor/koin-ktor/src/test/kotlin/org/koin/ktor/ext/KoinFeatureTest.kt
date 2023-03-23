//package org.koin.ktor.ext
//
//import io.ktor.application.*
//import io.ktor.server.testing.*
//import org.junit.Assert.*
//import org.junit.Ignore
//import org.junit.Test
//import org.koin.core.annotation.KoinReflectAPI
//import org.koin.core.context.GlobalContext
//import org.koin.core.context.loadKoinModules
//import org.koin.core.instance.newInstance
//import org.koin.core.parameter.emptyParametersHolder
//import org.koin.dsl.module
//import org.koin.dsl.single
//
///**
// * @author vinicius
// * @author Victor Alenkov
// *
// */
//class Foo(val name: String = "")
//class Bar(val name: String = "")
//class Bar2(val name: String = "")
//
//@OptIn(KoinReflectAPI::class)
//class KoinFeatureTest {
//
//    @Test
//    fun `can install feature`() {
//        val module = module {
//            single { Foo("bar") }
//        }
//        withApplication {
//            application.install(Koin) {
//                modules(module)
//            }
//            val bean = GlobalContext.get().get<Foo>()
//            assertNotNull(bean)
//        }
//    }
//
//    @Test
//    fun `Koin does not contain modules`() {
//        withApplication {
//            assertNull(GlobalContext.getOrNull())
//            assertNull(application.featureOrNull(Koin))
//
//            application.install(Koin)
//            assertNotNull(application.featureOrNull(Koin))
//            val koin = GlobalContext.getOrNull()
//            assertNotNull(koin)
//            requireNotNull(koin)
//
//            assertNull(koin.getOrNull<Foo>())
//        }
//    }
//
//    @Test
//    fun `add a Koin module to an already running application`() {
//        withApplication {
//            application.install(Koin)
//            val koin = application.getKoin()
//
//            assertNull(koin.getOrNull<Foo>())
//
//            application.featureOrNull(Koin)?.let {
//                loadKoinModules(module {
//                    single<Foo>()
//                })
//            }
//            assertNotNull(koin.getOrNull<Foo>())
//        }
//    }
//
//    @Test
//    fun `Using the koin extension`() {
//        withApplication {
//            assertNull(GlobalContext.getOrNull())
//            assertNull(application.featureOrNull(Koin))
//
//            application.koin {
//                modules(module {
//                    single<Foo>()
//                })
//            }
//            assertNotNull(application.getKoin().getOrNull<Foo>())
//        }
//    }
//
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
//}
