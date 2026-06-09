package org.koin.viewmodel

import androidx.lifecycle.ViewModel
import org.koin.core.logger.Level
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Regression test for issue #2370 / #2408 in the ViewModel resolution path.
 *
 * A ViewModel that receives a runtime parameter via parametersOf() AND a qualified
 * collaborator of an overlapping type (String here) must not have the qualified
 * dependency shadowed by the parameter. Before the qualifier guard in
 * CoreResolverV2.resolveFromStackedParameters, the qualified `get(named("Config"))`
 * was satisfied by the stacked "screenId" parameter instead of the registry definition.
 */
class ViewModelQualifiedParamTest {

    private val configQualifier = named("Config")
    private val globalConfig = "CONFIG_STRING"

    private class MyViewModel(val screenId: String, val config: String) : ViewModel()

    private val testModule = module {
        single(configQualifier) { globalConfig }

        viewModel { (screenId: String) ->
            MyViewModel(screenId = screenId, config = get(configQualifier))
        }
    }

    @Test
    fun viewModel_qualified_dependency_is_not_shadowed_by_parameter() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(testModule)
        }.koin

        val vm: MyViewModel = koin.get { parametersOf("screenId") }

        // The parameter must reach the ViewModel constructor param
        assertEquals("screenId", vm.screenId)

        // ...but the qualified dependency must come from the registry, NOT the parameter.
        // Bug: vm.config == "screenId"
        assertEquals(globalConfig, vm.config)
    }
}
