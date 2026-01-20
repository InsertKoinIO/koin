package org.koin.plugin.module.dsl

import androidx.lifecycle.ViewModel
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.KoinDslMarker
import org.koin.dsl.ScopeDSL


@KoinDslMarker
public fun <T : ViewModel> org.koin.core.module.Module.viewModel(): KoinDefinition<T> { TODO(USE_KOIN_COMPILER_PLUGIN) }

@KoinDslMarker
public fun <T : ViewModel> ScopeDSL.viewModel(): KoinDefinition<T> { TODO(USE_KOIN_COMPILER_PLUGIN) }