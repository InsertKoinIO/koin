package org.koin.test.android

import android.arch.lifecycle.ViewModel
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.android.viewmodel.experimental.builder.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module.module
import org.koin.experimental.builder.create
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest

/**
 * @author @fredy-mederos
 */
class ViewModelDSLWithParamsTest : AutoCloseKoinTest() {

    interface MyService {
        fun getData(id: Int): String
    }

    class MyServiceImpl(private val url: String) : MyService {
        override fun getData(id: Int): String {
            return "data from $url/$id"
        }
    }

    class MyViewModel(
        private val itemId: Int,
        private val service: MyService
    ) : ViewModel() {
        fun getData() = service.getData(itemId)
    }

    private val module = module {
        single<MyService> { create<MyServiceImpl>(it) } //create service with parameters
        viewModel<MyViewModel>() //create viewmodel with parameters
    }

    @Test
    fun view_model_parameters() {
        startKoin(listOf(module))

        //Getting MyViewModel and passing arguments for service url and itemId
        val vm1 = get<MyViewModel> { parametersOf("http://url.com", 10) }

        assertEquals("data from http://url.com/10", vm1.getData())
    }
}