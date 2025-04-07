package org.koin.test.android.scope

import org.koin.androidx.scope.ActivityScopeArchetype
import org.koin.androidx.scope.FragmentScopeArchetype
import org.koin.androidx.scope.ActivityRetainedScopeArchetype
import org.koin.androidx.scope.dsl.activityScope
import org.koin.androidx.scope.dsl.fragmentScope
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.core.annotation.KoinInternalApi
import org.koin.dsl.module
import kotlin.test.Test

@OptIn(KoinInternalApi::class)
class ScopeArchetypeDSLTest {

    class MyScopedClass
    class MyFactoryClass(val ms : MyScopedClass)

    @Test
    fun testArchetypeDSL_activity(){
        val module = module {
            activityScope {
                scoped { MyScopedClass() }
                factory { MyFactoryClass(get()) }
            }
        }

        assert(module.mappings.values.all {
            it.beanDefinition.scopeQualifier == ActivityScopeArchetype
        })
    }

    @Test
    fun testArchetypeDSL_activity_retained(){
        val module = module {
            activityRetainedScope {
                scoped { MyScopedClass() }
                factory { MyFactoryClass(get()) }
            }
        }

        assert(module.mappings.values.all {
            it.beanDefinition.scopeQualifier == ActivityRetainedScopeArchetype
        })
    }

    @Test
    fun testArchetypeDSL_fragment(){
        val module = module {
            fragmentScope {
                scoped { MyScopedClass() }
                factory { MyFactoryClass(get()) }
            }
        }

        assert(module.mappings.values.all {
            it.beanDefinition.scopeQualifier == FragmentScopeArchetype
        })
    }

}