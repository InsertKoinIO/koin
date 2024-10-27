package org.koin

import androidx.lifecycle.ViewModel
import org.koin.core.logger.Level
import org.koin.dsl.fu.factory
import org.koin.dsl.fu.viewModel
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class KoinFunctionalDSLTest {

    class MyViewModel : ViewModel()
    class MyPresenter

    @Test
    fun testFunctionParams() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    viewModel(::MyViewModel)
                    factory(::MyPresenter)
                }
            )
        }.koin

        assertNotNull(koin.getOrNull<MyViewModel>())
        assertNotEquals(koin.getOrNull<MyViewModel>(), koin.getOrNull<MyViewModel>())
    }
}