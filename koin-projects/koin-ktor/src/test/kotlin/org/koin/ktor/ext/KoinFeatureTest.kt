package org.koin.ktor.ext

import io.ktor.application.featureOrNull
import io.ktor.application.install
import io.ktor.server.testing.withApplication
import org.junit.Assert.*
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
    fun `can install feature`() {
        withApplication {
            application.install(Koin) {
                modules(module {
                    single<Foo>()
                })
            }
            assertNotNull(GlobalContext.get().getOrNull<Foo>())
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
}