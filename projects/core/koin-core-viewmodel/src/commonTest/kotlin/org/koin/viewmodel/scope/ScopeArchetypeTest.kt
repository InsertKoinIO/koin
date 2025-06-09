package org.koin.viewmodel.scope

import org.koin.core.component.getScopeId
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import org.koin.viewmodel.scope.ScopeArchetypeDSLTest.MyFactoryClass
import org.koin.viewmodel.scope.ScopeArchetypeDSLTest.MyScopedClass
import org.koin.viewmodel.scope.ScopeArchetypeDSLTest.MyViewModelClass
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ScopeArchetypeTest {

    @BeforeTest
    fun setup() {
        startKoin { printLogger(Level.DEBUG) }
    }

    @AfterTest
    fun stop() {
        stopKoin()
    }

    @Test
    fun testViewModelScope_creation() {
        val koin = KoinPlatform.getKoin()
        val vm = MyViewModelClass()

        val scope = vm.scope
        assertEquals(scope.scopeArchetype, ViewModelScopeArchetype)
        assertEquals(scope, koin.getScope(vm.getScopeId()))
    }

    @Test
    fun testViewModelScope_resolution() {
        val koin = KoinPlatform.getKoin()

        val module = module {
            viewModelScope {
                scoped { MyScopedClass() }
                factory { MyFactoryClass(get()) }
            }
        }
        koin.loadModules(listOf(module))
        val vm = MyViewModelClass()

        assertEquals(vm.scope.get<MyFactoryClass>().ms, vm.scope.get<MyScopedClass>())
    }

}
