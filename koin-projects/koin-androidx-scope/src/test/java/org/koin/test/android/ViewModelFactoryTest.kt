package org.koin.test.android

import androidx.lifecycle.Lifecycle
import org.junit.Assert
import org.junit.Test
import org.koin.androidx.scope.ScopeObserver
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstanceHolders

class ViewModelFactoryTest : AutoCloseKoinTest() {
    val module = module("module1") {
        single { MyService() }
    }

    class MyService

    @Test
    fun should_create_view_model() {
        startKoin(listOf(module))

        val service = get<MyService>()
        Assert.assertNotNull(service)

        val observer = ScopeObserver(Lifecycle.Event.ON_DESTROY, "testClass", "module1")

        assertDefinitions(1)
        assertRemainingInstanceHolders(1)

        observer.onDestroy()
        assertRemainingInstanceHolders(1)
    }
}