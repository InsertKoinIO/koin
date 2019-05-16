package org.koin.androidx.experimental.dsl

import androidx.lifecycle.ViewModel
import org.koin.androidx.viewmodel.dsl.setIsViewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.experimental.builder.create

/**
 * ViewModel DSL Extension
 * Allow to declare a ViewModel - be later inject into Activity/Fragment with dedicated injector
 *
 * @author Arnaud Giuliani
 *
 * @param qualifier - definition qualifier
 * @param override - allow definition override
 */
inline fun <reified T : ViewModel> Module.viewModel(
        qualifier: Qualifier? = null,
        override: Boolean = false
) {
    factory(qualifier, override) { create<T>(this) }.setIsViewModel()
}