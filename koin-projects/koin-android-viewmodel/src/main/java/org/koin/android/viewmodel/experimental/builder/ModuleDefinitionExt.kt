package org.koin.android.viewmodel.experimental.builder

import android.arch.lifecycle.ViewModel
import org.koin.dsl.context.ModuleDefinition
import org.koin.experimental.builder.create

/**
 * Create a Factory definition for given type T
 *
 * @param name
 * @param override - allow definition override
 */
inline fun <reified T : ViewModel> ModuleDefinition.viewModel(
    name: String = "",
    override: Boolean = false
) {
    factory(name, override) { create<T>() } bind ViewModel::class
}