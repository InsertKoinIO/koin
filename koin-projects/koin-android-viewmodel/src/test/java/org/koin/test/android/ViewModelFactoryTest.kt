package org.koin.test.android

import android.arch.lifecycle.ViewModel
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.android.viewmodel.ViewModelFactory
import org.koin.android.viewmodel.ViewModelParameters
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstanceHolders


class ViewModelFactoryTest : AutoCloseKoinTest() {

    val module = module {
        single { MyService() }
        viewModel { MyViewModel(get()) }
    }

    class MyService
    class MyViewModel(val service: MyService) : ViewModel()

    @Test
    fun should_create_view_model() {
        startKoin(listOf(module))

        ViewModelFactory.viewModelParameters = ViewModelParameters()
        val vm1 = ViewModelFactory.create(MyViewModel::class.java)

        ViewModelFactory.viewModelParameters = ViewModelParameters()
        val vm2 = ViewModelFactory.create(MyViewModel::class.java)
        val service = get<MyService>()

        assertEquals(vm1.service, vm2.service)
        assertEquals(service, vm2.service)

        assertContexts(1)
        assertDefinitions(2)
        assertRemainingInstanceHolders(1)
    }
}