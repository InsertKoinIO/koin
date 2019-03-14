package org.koin.test.android

import android.arch.lifecycle.Lifecycle
import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.android.scope.ScopeObserver
import org.koin.core.context.startKoin
import org.koin.core.error.ScopeIsClosedException
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest

class AndroidObserverTest : AutoCloseKoinTest() {

    class MyService

    @Test
    fun `should close scoped definition on ON_DESTROY`() {
        startKoin {
            printLogger(Level.DEBUG)
            modules(
                    module {
                        scoped { MyService() }
                    }
            )
        }

        val session = getKoin().createScope("session")
        val service = session.get<MyService>()
        Assert.assertNotNull(service)

        val observer = ScopeObserver(Lifecycle.Event.ON_DESTROY, "testClass", session)
        observer.onDestroy()

        try {
            session.get<MyService>()
            fail("no resolution of closed scope dependency")
        } catch (e: ScopeIsClosedException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `should not close scoped definition`() {
        startKoin {
            printLogger(Level.DEBUG)
            modules(org.koin.dsl.module {
                scoped { MyService() }
            })
        }

        val session = getKoin().createScope("session")
        val service = session.get<MyService>()
        Assert.assertNotNull(service)

        val observer = ScopeObserver(Lifecycle.Event.ON_DESTROY, "testClass", session)
        observer.onStop()

        session.get<MyService>()
    }

    @Test
    fun `should close scoped definition on ON_STOP`() {
        startKoin {
            printLogger(Level.DEBUG)
            modules(org.koin.dsl.module {
                scoped { MyService() }
            })
        }

        val session = getKoin().createScope("session")
        val service = session.get<MyService>()
        Assert.assertNotNull(service)

        val observer = ScopeObserver(Lifecycle.Event.ON_STOP, "testClass", session)
        observer.onStop()

        try {
            session.get<MyService>()
            fail("no resolution of closed scope dependency")
        } catch (e: ScopeIsClosedException) {
            e.printStackTrace()
        }
    }
}