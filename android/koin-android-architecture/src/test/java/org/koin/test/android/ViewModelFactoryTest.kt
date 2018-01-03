package org.koin.test.android

import android.arch.lifecycle.ViewModel
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.android.architecture.ext.KoinFactory
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AbstractKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances


class ViewModelFactoryTest : AbstractKoinTest() {

    val module = applicationContext {
        bean { MyService() }
        viewModel { MyViewModel(get()) }
    }

    class MyService
    class MyViewModel(val service: MyService) : ViewModel()

    @Test
    fun should_create_view_model() {
        startKoin(listOf(module))

        val vm1 = KoinFactory.create(MyViewModel::class.java)
        val vm2 = KoinFactory.create(MyViewModel::class.java)
        val service = get<MyService>()

        assertEquals(vm1.service, vm2.service)
        assertEquals(service, vm2.service)

        assertContexts(1)
        assertDefinitions(2)
        assertRemainingInstances(1)
    }
}