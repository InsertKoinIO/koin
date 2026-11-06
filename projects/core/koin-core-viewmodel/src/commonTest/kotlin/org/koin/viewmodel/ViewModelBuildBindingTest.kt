package org.koin.viewmodel

import androidx.lifecycle.ViewModel
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.plugin.module.dsl.buildViewModel
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Regression test for #2474: buildViewModel used to bind every ViewModel definition to the shared
 * androidx.lifecycle.ViewModel secondary type. When ViewModels were declared in 2+ modules, each
 * module re-indexed that shared key, so strictOverride() failed with DefinitionOverrideException
 * even though no definition was declared twice - and in default mode get<ViewModel>() returned
 * whichever module was loaded last.
 *
 * buildViewModel must behave like the classic viewModel{} / viewModelOf(::) DSL: register the
 * concrete ViewModel type only, with no secondary binding.
 */
@OptIn(KoinInternalApi::class)
class ViewModelBuildBindingTest {

    class FirstVM : ViewModel()
    class SecondVM : ViewModel()

    @Test
    fun buildViewModel_in_multiple_modules_starts_with_strictOverride_issue_2474() {
        val koin = koinApplication {
            strictOverride()
            modules(
                module { buildViewModel(FirstVM::class) { FirstVM() } },
                module { buildViewModel(SecondVM::class) { SecondVM() } },
            )
        }.koin

        assertNotNull(koin.get<FirstVM>())
        assertNotNull(koin.get<SecondVM>())
    }

    @Test
    fun buildViewModel_scoped_in_multiple_modules_starts_with_strictOverride_issue_2474() {
        val koin = koinApplication {
            strictOverride()
            modules(
                module { scope(named("screen")) { buildViewModel(FirstVM::class) { FirstVM() } } },
                module { scope(named("screen")) { buildViewModel(SecondVM::class) { SecondVM() } } },
            )
        }.koin

        val scope = koin.createScope("screen-1", named("screen"))
        assertNotNull(scope.get<FirstVM>())
        assertNotNull(scope.get<SecondVM>())
    }

    @Test
    fun buildViewModel_does_not_register_secondary_ViewModel_binding_issue_2474() {
        val koin = koinApplication {
            modules(module { buildViewModel(FirstVM::class) { FirstVM() } })
        }.koin

        // only the concrete type must be indexed - no shared androidx.lifecycle.ViewModel key
        assertNull(koin.getOrNull<ViewModel>())
        assertNotNull(koin.get<FirstVM>())
    }
}
