package org.koin.ktor.ext

import io.ktor.application.featureOrNull
import io.ktor.application.install
import io.ktor.server.testing.withApplication
import org.junit.Assert
import org.junit.Test
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

/**
 * @author vinicius
 * @author Victor Alenkov
 *
 */
class Foo(val name: String)

class KoinFeatureTest {

    @Test
    fun `Koin does not contain modules`() {
        withApplication {
            Assert.assertNull(GlobalContext.getOrNull()?.koin)
            Assert.assertNull(application.featureOrNull(Koin))

            application.install(Koin)
            Assert.assertNotNull(application.featureOrNull(Koin))
            val koin = GlobalContext.getOrNull()?.koin
            Assert.assertNotNull(koin)
            requireNotNull(koin)

            Assert.assertNull(koin.getOrNull<Foo>())
        }
    }

    @Test
    fun `can install feature`() {
        withApplication {
            application.install(Koin) {
                modules(module {
                    single { Foo("bar") }
                })
            }
            Assert.assertNotNull(GlobalContext.get().koin.getOrNull<Foo>())
        }
    }

    @Test
    fun `add a Koin module to an already running application`() {
        withApplication {
            application.install(Koin)
            val koin = GlobalContext.get().koin

            Assert.assertNull(koin.getOrNull<Foo>())

            application.featureOrNull(Koin)?.let {
                loadKoinModules(module {
                    single { Foo(name = "bar") }
                })
            }
            Assert.assertNotNull(koin.getOrNull<Foo>())
        }
    }
}