package org.koin.android.architecture.ext.koin

import android.arch.lifecycle.ViewModel
import org.koin.core.bean.Definition
import org.koin.dsl.context.Context


/**
 * ViewModel DSL Extension
 * Allow to declare a vieModel - be later inject into Activity/Fragment with dedicated injector
 */
inline fun <reified T : ViewModel> Context.viewModel(
    name: String = "",
    noinline definition: Definition<T>
) {
    val bean = factory(name, definition)
    bean.bind(ViewModel::class)
}