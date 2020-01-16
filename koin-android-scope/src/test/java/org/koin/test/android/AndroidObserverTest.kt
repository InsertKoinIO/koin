package org.koin.test.android

import android.arch.lifecycle.Lifecycle
import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.android.scope.ScopeObserver
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest

class AndroidObserverTest : AutoCloseKoinTest() {

    class MyService

    val scopeKey = named("SCOPE_KEY")

    val module = module {
        scope(scopeKey) {
            scoped { MyService() }
        }
    }

    @Test
    fun `should close scoped definition on ON_DESTROY`() {
        startKoin {
            printLogger(Level.DEBUG)
            modules(module)
        }

        val session = getKoin().createScope("session", scopeKey)
        val service = session.get<MyService>()
        Assert.assertNotNull(service)

        val observer = ScopeObserver(Lifecycle.Event.ON_DESTROY, "testClass", session)
        observer.onDestroy()

        try {
            session.get<MyService>()
            fail("no resolution of closed scope dependency")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun `should not close scoped definition`() {
        startKoin {
            printLogger(Level.DEBUG)
            modules(module)
        }

        val session = getKoin().createScope("session", scopeKey)
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
            modules(module)
        }

        val session = getKoin().createScope("session", scopeKey)
        val service = session.get<MyService>()
        Assert.assertNotNull(service)

        val observer = ScopeObserver(Lifecycle.Event.ON_STOP, "testClass", session)
        observer.onStop()

        try {
            session.get<MyService>()
            fail("no resolution of closed scope dependency")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}