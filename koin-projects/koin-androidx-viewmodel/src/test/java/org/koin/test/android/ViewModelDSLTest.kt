package org.koin.test.android

import androidx.lifecycle.ViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.koin.androidx.viewmodel.experimental.builder.viewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstanceHolders

class ViewModelDSLTest : AutoCloseKoinTest() {

    val module = module {
        single { MyService() }
        viewModel { MyViewModel(get()) }
    }

    val moduleAB = module {
        single { MyService() }
        viewModel<MyViewModel>()
    }

    val module2 = module {
        viewModel { (url: String) -> MyViewModel2(url) }
    }

    class MyService
    class MyViewModel(val service: MyService) : ViewModel()

    class MyViewModel2(val url: String) : ViewModel()

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
        assertRemainingInstanceHolders(2)
    }

    @Test
    fun should_inject_built_view_model() {
        startKoin(listOf(moduleAB))

        val vm1 = get<MyViewModel>()
        val vm2 = get<MyViewModel>()
        val service = get<MyService>()

        assertEquals(vm1.service, vm2.service)
        assertEquals(service, vm2.service)

        assertContexts(1)
        assertDefinitions(2)
        assertRemainingInstanceHolders(2)
    }

    @Test
    fun view_model_is_factory() {
        startKoin(listOf(module))

        val vm1 = get<MyViewModel>()
        val vm2 = get<MyViewModel>()

        assertNotEquals(vm1, vm2)

        assertContexts(1)
        assertDefinitions(2)
        assertRemainingInstanceHolders(2)
    }

    @Test
    fun view_model_parameters() {
        startKoin(listOf(module2))

        val url = "http://..."

        val vm1 = get<MyViewModel2> { parametersOf(url) }

        assertEquals(url, vm1.url)

        assertContexts(1)
        assertDefinitions(1)
        assertRemainingInstanceHolders(1)
    }
}