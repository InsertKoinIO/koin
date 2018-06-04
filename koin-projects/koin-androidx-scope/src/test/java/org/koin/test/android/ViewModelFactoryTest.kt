package org.koin.test.android

import org.junit.Assert
import org.junit.Test
import org.koin.android.architectre.ScopeObserver
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances

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

        val observer = ScopeObserver("testClass", arrayOf("module1"))

        assertDefinitions(1)
        assertRemainingInstances(1)

        observer.onDestroy()
        assertRemainingInstances(0)
    }
}