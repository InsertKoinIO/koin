package org.koin.test.android

import android.arch.lifecycle.Lifecycle
import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.android.scope.ScopeObserver
import org.koin.core.error.ScopeNotCreatedException
import org.koin.core.logger.Level
import org.koin.dsl.koinApplication
import org.koin.test.AutoCloseKoinTest


class AndroidObserverTest : AutoCloseKoinTest() {

    class MyService

    @Test
    fun `should close scoped definition on ON_DESTROY`() {
        val koin = koinApplication {
            useLogger(Level.DEBUG)
            loadModules(org.koin.dsl.module {
                scope("session") { MyService() }
            })
        }.koin

        val session = getKoin().createScope("session")
        val service = koin.get<MyService>()
        Assert.assertNotNull(service)

        val observer = ScopeObserver(Lifecycle.Event.ON_DESTROY, "testClass", session)
        observer.onDestroy()

        try {
            koin.get<MyService>()
            fail("no resolution of closed scope dependency")
        } catch (e: ScopeNotCreatedException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `should not close scoped definition`() {
        val koin = koinApplication {
            useLogger(Level.DEBUG)
            loadModules(org.koin.dsl.module {
                scope("session") { MyService() }
            })
        }.koin

        val session = getKoin().createScope("session")
        val service = koin.get<MyService>()
        Assert.assertNotNull(service)

        val observer = ScopeObserver(Lifecycle.Event.ON_DESTROY, "testClass", session)
        observer.onStop()

        koin.get<MyService>()
    }

    @Test
    fun `should close scoped definition on ON_STOP`() {
        val koin = koinApplication {
            useLogger(Level.DEBUG)
            loadModules(org.koin.dsl.module {
                scope("session") { MyService() }
            })
        }.koin

        val session = getKoin().createScope("session")
        val service = koin.get<MyService>()
        Assert.assertNotNull(service)

        val observer = ScopeObserver(Lifecycle.Event.ON_STOP, "testClass", session)
        observer.onStop()

        try {
            koin.get<MyService>()
            fail("no resolution of closed scope dependency")
        } catch (e: ScopeNotCreatedException) {
            e.printStackTrace()
        }
    }
}