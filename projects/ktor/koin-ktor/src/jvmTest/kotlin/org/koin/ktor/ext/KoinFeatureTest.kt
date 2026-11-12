package org.koin.ktor.ext

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopping
import io.ktor.server.application.install
import io.ktor.server.testing.TestApplication
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.ktor.plugin.KoinApplicationStopped
import org.koin.ktor.plugin.KoinIsolated
import org.koin.mp.KoinPlatform
import kotlin.test.Test
import kotlin.test.assertEquals
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

    @Test
    fun `does not close koin when another application stops`() = runTest {
        val module = module {
            single { Foo("bar") }
        }
        val otherApplication = TestApplication { application { } }
        otherApplication.start()
        val testApplication = TestApplication {
            application {
                install(Koin) {
                    modules(module)
                }
            }
        }
        testApplication.start()
        assertNotNull(KoinPlatform.getKoinOrNull()?.getOrNull<Foo>())

        testApplication.application.monitor.raise(ApplicationStopping, otherApplication.application)

        assertNotNull(KoinPlatform.getKoinOrNull()?.getOrNull<Foo>())

        testApplication.application.monitor.raise(ApplicationStopping, testApplication.application)

        assertNull(KoinPlatform.getKoinOrNull()?.getOrNull<Foo>())
        runCatching { testApplication.stop() }
        runCatching { otherApplication.stop() }
    }

    @Test
    fun `stopping the application frees the global context`() = runTest {
        val testApplication = TestApplication { application { install(Koin) } }
        testApplication.start()

        testApplication.application.monitor.raise(ApplicationStopping, testApplication.application)

        assertNull(KoinPlatform.getKoinOrNull())
        startKoin { }
        runCatching { testApplication.stop() }
    }

    // The monitor outlives every application of a reloading server, so a subscription left on it
    // keeps its stopped application and Koin reachable.
    @Test
    fun `stop subscription is released once the application has stopped`() = runTest {
        val testApplication = TestApplication { application { install(Koin) } }
        testApplication.start()
        val application = testApplication.application
        var stops = 0
        application.monitor.subscribe(KoinApplicationStopped) { stops++ }

        application.monitor.raise(ApplicationStopping, application)
        application.monitor.raise(ApplicationStopping, application)

        assertEquals(1, stops)
        runCatching { testApplication.stop() }
    }

    // Ktor 3.5 reloads by starting the new application before it stops the previous one.
    @Test
    fun `stopping the previous application keeps the Koin of the one that replaced it`() = runTest {
        val previousApplication = TestApplication { application { install(Koin) } }
        previousApplication.start()
        val testApplication = TestApplication {
            application {
                install(Koin) { modules(module { single { Foo("bar") } }) }
            }
        }
        testApplication.start()

        previousApplication.application.monitor.raise(ApplicationStopping, previousApplication.application)

        assertNotNull(KoinPlatform.getKoinOrNull()?.getOrNull<Foo>())
        runCatching { testApplication.stop() }
        runCatching { previousApplication.stop() }
    }

    @Test
    fun `stopping an isolated application keeps the global Koin`() = runTest {
        startKoin { modules(module { single { Foo("bar") } }) }
        val testApplication = TestApplication { application { install(KoinIsolated) } }
        testApplication.start()

        testApplication.application.monitor.raise(ApplicationStopping, testApplication.application)

        assertNotNull(KoinPlatform.getKoinOrNull()?.getOrNull<Foo>())
        runCatching { testApplication.stop() }
    }
}
