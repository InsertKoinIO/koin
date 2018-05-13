package org.koin.test.android

import android.arch.lifecycle.ViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.koin.android.architecture.ext.koin.viewModel
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances

class ViewModelDSLTest : AutoCloseKoinTest() {

    val module = module {
        bean { MyService() }
        viewModel { MyViewModel(get()) }
    }

    val module2 = module {
        viewModel { p -> MyViewModel2(p["url"]) }
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

    @Test
    fun view_model_parameters() {
        startKoin(listOf(module2))

        val url = "http://..."

        val vm1 = get<MyViewModel2> { mapOf("url" to url) }

        assertEquals(url, vm1.url)

        assertContexts(1)
        assertDefinitions(1)
        assertRemainingInstances(0)
    }
}