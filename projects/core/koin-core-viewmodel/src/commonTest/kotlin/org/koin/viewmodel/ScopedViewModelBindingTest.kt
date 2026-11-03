package org.koin.viewmodel

import androidx.lifecycle.ViewModel
import org.koin.core.annotation.KoinInternalApi
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.plugin.module.dsl.buildViewModel
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals

// Compiler-plugin DSL: a ViewModel generated in a scope must be bound to ViewModel, like its module-level twin.
@OptIn(KoinInternalApi::class)
class ScopedViewModelBindingTest {

    class ScopeOwner
    class FirstViewModel : ViewModel()
    class SecondViewModel : ViewModel()

    @Test
    fun scoped_view_model_is_bound_to_view_model() {
        val module = module {
            scope<ScopeOwner> { buildViewModel(FirstViewModel::class) { FirstViewModel() } }
        }

        val definition = module.mappings.values.first { it.beanDefinition.primaryType == FirstViewModel::class }.beanDefinition
        assertEquals(listOf<KClass<*>>(ViewModel::class), definition.secondaryTypes)
    }

    @Test
    fun scoped_view_models_are_all_resolved_as_view_model() {
        val koin = koinApplication {
            modules(module {
                scope<ScopeOwner> {
                    buildViewModel(FirstViewModel::class) { FirstViewModel() }
                    buildViewModel(SecondViewModel::class) { SecondViewModel() }
                }
            })
        }.koin

        val scope = koin.createScope<ScopeOwner>("owner")
        assertEquals(2, scope.getAll<ViewModel>().size)
    }
}
