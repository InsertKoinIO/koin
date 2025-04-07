package org.koin.viewmodel.scope

import org.koin.core.annotation.KoinInternalApi
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(KoinInternalApi::class)
class ScopeArchetypeDSLTest {

    class MyScopedClass
    class MyFactoryClass(val ms: MyScopedClass)
    class MyViewModelClass : ScopeViewModel()

    @Test
    fun testArchetypeDSL_activity() {
        val module = module {
            viewModelScope {
                scoped { MyScopedClass() }
                factory { MyFactoryClass(get()) }
            }
        }

        assertTrue(module.mappings.values.all {
            it.beanDefinition.scopeQualifier == ViewModelScopeArchetype
        })
    }

}