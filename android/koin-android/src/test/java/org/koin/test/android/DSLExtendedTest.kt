package org.koin.test.android

import androidx.lifecycle.ViewModel
import org.junit.Test
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.scopedOf
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MyAdapter()
class MyViewModel(val adapter: MyAdapter) : ViewModel()
class ScopedComponent()
class MyScope

class DSLExtendedTest {

    @Test
    fun `android dsl`(){
        val m = module {
            factoryOf(::MyAdapter)
            viewModelOf(::MyViewModel)
            viewModelOf(::MyViewModel){
                named("bis")
            }
            scope<MyScope> {
                scopedOf(::ScopedComponent)
            }
        }
        val koin = koinApplication {
            modules(m)
        }.koin
        assertTrue(koin.getOrNull<MyViewModel>() != koin.getOrNull<MyViewModel>(named("bis")))
        assertNotNull(koin.getOrNull<MyViewModel>())
        assertNotNull(koin.getOrNull<MyViewModel>()?.adapter)
        val scope = koin.createScope<MyScope>()
        assertNotNull(scope.getOrNull<ScopedComponent>())
    }

}