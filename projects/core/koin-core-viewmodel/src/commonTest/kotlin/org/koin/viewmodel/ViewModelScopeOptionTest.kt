package org.koin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.CreationExtras
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.annotation.KoinViewModelScopeApi
import org.koin.core.error.NoDefinitionFoundException
import org.koin.core.module.dsl.viewModel
import org.koin.core.option.viewModelScopeFactory
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.viewmodel.scope.viewModelScope
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * #2417(b): a ViewModel declared inside viewModelScope { } resolves from _root_ and throws an
 * opaque NoDefinitionFound when the viewModelScopeFactory() option is not enabled. The option is
 * required for viewModelScope { } to be resolvable; the error should say so.
 */
@OptIn(KoinInternalApi::class, KoinExperimentalAPI::class, KoinViewModelScopeApi::class)
class ViewModelScopeOptionTest {

    class MyVM : ViewModel()
    class UnregisteredVM : ViewModel()

    @Test
    fun viewModelScope_without_factory_option_gives_actionable_error_2417b() {
        val koin = koinApplication {
            modules(module { viewModelScope { viewModel { MyVM() } } })
        }.koin

        val error = assertFailsWith<Throwable> {
            resolveViewModel(MyVM::class, ViewModelStore(), null, CreationExtras.Empty, null, koin.scopeRegistry.rootScope)
        }
        val msg = error.message ?: ""
        assertTrue(
            msg.contains("viewModelScopeFactory"),
            "expected guidance to enable viewModelScopeFactory(), got: $msg",
        )
    }

    @Test
    fun viewModelScope_with_factory_option_resolves() {
        val koin = koinApplication {
            options(viewModelScopeFactory())
            modules(module { viewModelScope { viewModel { MyVM() } } })
        }.koin

        val vm = resolveViewModel(MyVM::class, ViewModelStore(), null, CreationExtras.Empty, null, koin.scopeRegistry.rootScope)
        assertNotNull(vm)
    }

    @Test
    fun genuinely_missing_viewmodel_keeps_generic_error() {
        val koin = koinApplication { }.koin

        // UnregisteredVM is not declared anywhere → must stay a plain NoDefinitionFound,
        // not the viewModelScopeFactory hint (guards against over-firing).
        val error = assertFailsWith<NoDefinitionFoundException> {
            resolveViewModel(UnregisteredVM::class, ViewModelStore(), null, CreationExtras.Empty, null, koin.scopeRegistry.rootScope)
        }
        assertTrue((error.message ?: "").contains("No definition found"))
    }
}
