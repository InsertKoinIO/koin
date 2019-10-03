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
        objectScope<MyService>()
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
        val instance = MyService()
        val serviceSession = getKoin().createObjectScoped(instance)

        val service = session.get<MyService>()
        val serviceInstance = serviceSession.get<MyService>()

        Assert.assertNotNull(service)
        Assert.assertEquals(instance, serviceInstance)

        arrayOf(serviceSession, session).forEach {
            ScopeObserver(Lifecycle.Event.ON_DESTROY, "testClass", it).onStop()
        }

        session.get<MyService>()
        serviceSession.get<MyService>()
    }

    @Test
    fun `should close scoped definition on ON_STOP`() {
        startKoin {
            printLogger(Level.DEBUG)
            modules(module)
        }

        val session = getKoin().createScope("session", scopeKey)
        val instance = MyService()
        val serviceSession = getKoin().createObjectScoped(instance)
        val service = session.get<MyService>()
        val serviceInstance = serviceSession.get<MyService>()

        Assert.assertNotNull(service)
        Assert.assertEquals(instance, serviceInstance)

        arrayOf(serviceSession, session).forEach {
            ScopeObserver(Lifecycle.Event.ON_STOP, "testClass", it).onStop()
        }

        try {
            session.get<MyService>()
            fail("no resolution of closed scope dependency")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            serviceSession.get<MyService>()
            fail("no resolution of closed scope dependency")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}