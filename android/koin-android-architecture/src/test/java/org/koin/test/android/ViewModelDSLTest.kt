package org.koin.test.android

import android.arch.lifecycle.ViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AbstractKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances
import viewModel

class ViewModelDSLTest : AbstractKoinTest() {

    val module = applicationContext {
        bean { MyService() }
        viewModel { MyViewModel(get()) }
    }

    class MyService
    class MyViewModel(val service: MyService) : ViewModel()

    @Test
    fun should_inject_view_model() {
        startKoin(listOf(module))

        val vm1 = get<MyViewModel>()
        val vm2 = get<MyViewModel>()
        val service = get<MyService>()

        assertEquals(vm1.service, vm2.service)
        assertEquals(service, vm2.service)

        assertContexts(1)
        assertDefinitions(2)
        assertRemainingInstances(1)
    }

    @Test
    fun view_model_is_factory() {
        startKoin(listOf(module))

        val vm1 = get<MyViewModel>()
        val vm2 = get<MyViewModel>()

        assertNotEquals(vm1, vm2)

        assertContexts(1)
        assertDefinitions(2)
        assertRemainingInstances(1)
    }
}