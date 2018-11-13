package org.koin.android.viewmodel.experimental.builder

import android.arch.lifecycle.ViewModel
import org.koin.android.viewmodel.viewModel
import org.koin.core.module.Module
import org.koin.experimental.builder.create

/**
 * Create a Factory definition for given type T
 *
 * @param name
 * @param override - allow definition override
 */
inline fun <reified T : ViewModel> Module.viewModel(
    name: String = "",
    override: Boolean = false
) {
    viewModel(name, override) { create<T>() }
}