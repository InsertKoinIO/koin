package org.koin.ktor.ext

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.testing.TestApplication
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.ktor.plugin.KoinIsolated
import org.koin.mp.KoinPlatform
import kotlin.test.Test

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
    fun after() {
        stopKoin()
    }

    @Test
    fun `can install feature`() = runTest {
        val module = module {
            single { Foo("bar") }
        }
        val application = TestApplication {
            application {
                install(Koin) {
                    modules(module)
                }
            }
        }
        application.start()
        val bean = KoinPlatform.getKoin().getOrNull<Foo>()
        assertNotNull(bean)
        runCatching { application.stop() }
    }

    @Test
    fun `can install feature - isolated context`() = runTest {
        val module = module {
            single { Foo("bar") }
        }
        var application: Application? = null
        val testApplication = TestApplication {
            application {
                install(KoinIsolated) {
                    modules(module)
                }
                application = this
            }
        }
        testApplication.start()
        // Isolated context should be limited to the application scope only
        val bean1 = application?.get<Foo>()
        assertNotNull(bean1)
        // Isolated Koin will not be set to the global scope
        val bean2 = runCatching { KoinPlatform.getKoin().getOrNull<Foo>() }.getOrNull()
        assertNull(bean2)
        runCatching { testApplication.stop() }
    }
}
