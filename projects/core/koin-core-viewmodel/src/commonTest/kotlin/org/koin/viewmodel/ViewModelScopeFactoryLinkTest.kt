package org.koin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.CreationExtras
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.option.viewModelScopeFactory
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame

/**
 * Regression test for #2299: with viewModelScopeFactory() enabled, a ViewModel declared in a
 * custom scope must resolve. KoinViewModelFactory creates a fresh vmScope (ViewModelScopeArchetype)
 * but did not link it to the parent (custom) scope, so the ViewModel — and its scoped deps,
 * registered under the custom scope qualifier — were unreachable.
 */
@OptIn(KoinInternalApi::class, KoinExperimentalAPI::class)
class ViewModelScopeFactoryLinkTest {

    class Example
    class ScopedDep
    class ScopedVM(val dep: ScopedDep) : ViewModel()

    @Test
    fun viewModelScopeFactory_resolves_viewModel_from_custom_scope_issue_2299() {
        val koin = koinApplication {
            options(viewModelScopeFactory())
            modules(
                module {
                    scope<Example> {
                        scopedOf(::ScopedDep)
                        viewModelOf(::ScopedVM)
                    }
                },
            )
        }.koin

        val parentScope = koin.createScope("example-1", TypeQualifier(Example::class), Example())

        val vm = resolveViewModel(
            ScopedVM::class, ViewModelStore(), null, CreationExtras.Empty, null, parentScope,
        )

        assertNotNull(vm)
        // its scoped dependency must come from the parent custom scope
        assertNotNull(vm.dep)
        assertSame(parentScope.get<ScopedDep>(), vm.dep)
    }
}
