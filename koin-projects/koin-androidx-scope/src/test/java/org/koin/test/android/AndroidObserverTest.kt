package org.koin.test.android

import androidx.lifecycle.Lifecycle
import org.junit.Assert
import org.junit.Test
import org.koin.androidx.scope.ScopeObserver
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.standalone.getKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstanceHolders

class AndroidObserverTest : AutoCloseKoinTest() {

    val module = module("module1") {
        scope("session") { MyService() }
    }

    class MyService

    @Test
    fun should_create_view_model() {
        startKoin(listOf(module))

        val session = getKoin().createScope("session")
        val service = get<MyService>(scope = session)
        Assert.assertNotNull(service)

        val observer = ScopeObserver(Lifecycle.Event.ON_DESTROY, "testClass", session)

        assertDefinitions(1)
        assertRemainingInstanceHolders(1)

        observer.onDestroy()
        assertRemainingInstanceHolders(0)

        Assert.assertTrue(session.isClosed)
    }
}