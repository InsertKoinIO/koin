package org.koin.ktor.ext

import io.ktor.application.install
import io.ktor.server.testing.withApplication
import io.ktor.server.testing.withTestApplication
import org.junit.Assert
import org.junit.Test
import org.koin.core.context.GlobalContext
import org.koin.dsl.module

/**
 * @author vinicius
 * @author Victor Alenkov
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

    @Test
    fun `custom stop listeners`() {
        val module = module {
            single { Foo("bar") }
        }
        var c = 0
        withTestApplication {
            Assert.assertEquals(0, c)

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
            val bean = GlobalContext.get().koin.get<Foo>()
            Assert.assertNotNull(bean)
            Assert.assertEquals(0, c)
        }
        Assert.assertEquals(3, c)

        withTestApplication {
            Assert.assertEquals(3, c)
            application.install(Koin) {
                modules(module)
                environment.monitor.subscribe(KoinApplicationStarted) {
                    c = 0
                }
                environment.monitor.subscribe(KoinApplicationStopped) {
                    c += 4
                }
            }
            val bean = GlobalContext.get().koin.get<Foo>()
            Assert.assertNotNull(bean)
            Assert.assertEquals(0, c)
        }
        Assert.assertEquals(4, c)

    }
}
