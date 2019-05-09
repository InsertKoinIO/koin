package org.koin.ktor.ext

import io.ktor.application.install
import io.ktor.server.testing.withApplication
import org.junit.Assert
import org.junit.Test
import org.koin.core.context.GlobalContext
import org.koin.dsl.module

/**
 * @author vinicius
 *
 */
class Foo(val name: String)

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
            val bean = GlobalContext.get().koin.get<Foo>()
            Assert.assertNotNull(bean)
        }
    }
}