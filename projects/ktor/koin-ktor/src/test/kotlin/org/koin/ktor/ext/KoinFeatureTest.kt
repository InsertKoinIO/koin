package org.koin.ktor.ext

import io.ktor.server.application.*
import io.ktor.server.testing.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.ktor.plugin.KoinIsolated
import org.koin.mp.KoinPlatform

/**
 * @author vinicius
 * @author Victor Alenkov
 *
 */
class Foo(val name: String = "")
class Bar(val name: String = "")
class Bar2(val name: String = "")

class KoinFeatureTest {

    @After
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
}
