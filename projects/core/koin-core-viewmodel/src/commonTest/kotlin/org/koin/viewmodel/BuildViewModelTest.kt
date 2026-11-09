package org.koin.viewmodel

import androidx.lifecycle.ViewModel
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.plugin.module.dsl.buildViewModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

// #2474: buildViewModel indexes a ViewModel under its own type only, like the classic viewModel { } DSL,
// so ViewModels declared in different modules never collide.
@OptIn(KoinInternalApi::class)
class BuildViewModelTest {

    class ScopeOwner
    class FirstViewModel : ViewModel()
    class SecondViewModel : ViewModel()

    private class WarningCollector : Logger(Level.WARNING) {
        val warnings = mutableListOf<String>()
        override fun display(level: Level, msg: MESSAGE) {
            if (level == Level.WARNING) warnings += msg
        }
    }

    private fun Module.firstViewModelDefinition() =
        mappings.values.first { it.beanDefinition.primaryType == FirstViewModel::class }.beanDefinition

    @Test
    fun view_model_is_indexed_like_classic_view_model() {
        val plugin = module { buildViewModel(FirstViewModel::class) { FirstViewModel() } }
        val classic = module { viewModel { FirstViewModel() } }

        assertEquals(classic.mappings.keys, plugin.mappings.keys)
        assertTrue(plugin.firstViewModelDefinition().secondaryTypes.isEmpty())
    }

    @Test
    fun qualified_view_model_is_indexed_like_classic_view_model() {
        val plugin = module { buildViewModel(FirstViewModel::class, named("home")) { FirstViewModel() } }
        val classic = module { viewModel(named("home")) { FirstViewModel() } }

        assertEquals(classic.mappings.keys, plugin.mappings.keys)
        assertTrue(plugin.firstViewModelDefinition().secondaryTypes.isEmpty())
    }

    @Test
    fun scoped_view_model_is_indexed_like_classic_view_model() {
        val plugin = module { scope<ScopeOwner> { buildViewModel(FirstViewModel::class) { FirstViewModel() } } }
        val classic = module { scope<ScopeOwner> { viewModel { FirstViewModel() } } }

        assertEquals(classic.mappings.keys, plugin.mappings.keys)
        assertTrue(plugin.firstViewModelDefinition().secondaryTypes.isEmpty())
    }

    @Test
    fun view_model_supertype_does_not_resolve() {
        val koin = koinApplication {
            modules(module { buildViewModel(FirstViewModel::class) { FirstViewModel() } })
        }.koin

        assertNull(koin.getOrNull<ViewModel>())
    }

    @Test
    fun view_models_in_two_modules_start_under_strict_override() {
        val koin = koinApplication {
            strictOverride()
            modules(
                module { buildViewModel(FirstViewModel::class) { FirstViewModel() } },
                module { buildViewModel(SecondViewModel::class) { SecondViewModel() } },
            )
        }.koin

        assertIs<FirstViewModel>(koin.get<FirstViewModel>())
        assertIs<SecondViewModel>(koin.get<SecondViewModel>())
    }

    @Test
    fun scoped_view_models_of_one_scope_in_two_modules_start_under_strict_override() {
        val koin = koinApplication {
            strictOverride()
            modules(
                module { scope<ScopeOwner> { buildViewModel(FirstViewModel::class) { FirstViewModel() } } },
                module { scope<ScopeOwner> { buildViewModel(SecondViewModel::class) { SecondViewModel() } } },
            )
        }.koin

        val scope = koin.createScope<ScopeOwner>("owner")
        assertIs<FirstViewModel>(scope.get<FirstViewModel>())
        assertIs<SecondViewModel>(scope.get<SecondViewModel>())
    }

    @Test
    fun view_models_in_two_modules_log_no_override_warning() {
        val logger = WarningCollector()
        koinApplication {
            logger(logger)
            modules(
                module { buildViewModel(FirstViewModel::class) { FirstViewModel() } },
                module { buildViewModel(SecondViewModel::class) { SecondViewModel() } },
            )
        }

        assertTrue(logger.warnings.none { "override" in it }, "unexpected override warnings: ${logger.warnings}")
    }

    @Test
    fun same_view_model_in_two_modules_still_fails_under_strict_override() {
        assertFailsWith<DefinitionOverrideException> {
            koinApplication {
                strictOverride()
                modules(
                    module { buildViewModel(FirstViewModel::class) { FirstViewModel() } },
                    module { buildViewModel(FirstViewModel::class) { FirstViewModel() } },
                )
            }
        }
    }
}
