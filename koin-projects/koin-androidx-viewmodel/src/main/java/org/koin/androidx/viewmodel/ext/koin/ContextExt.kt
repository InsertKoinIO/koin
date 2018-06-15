package org.koin.androidx.viewmodel.ext.koin

import androidx.lifecycle.ViewModel
import org.koin.dsl.context.ModuleDefinition
import org.koin.dsl.definition.Definition


/**
 * ViewModel DSL Extension
 * Allow to declare a vieModel - be later inject into Activity/Fragment with dedicated injector
 */
inline fun <reified T : ViewModel> ModuleDefinition.viewModel(
    name: String = "",
    noinline definition: Definition<T>
) {
    val bean = factory(name, false, definition)
    bean.bind(ViewModel::class)
}