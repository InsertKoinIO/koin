package org.koin.plugin.module.dsl

import androidx.work.ListenableWorker
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.KoinDslMarker
import org.koin.core.module.Module
import org.koin.dsl.ScopeDSL

@KoinDslMarker
public fun <T : ListenableWorker> Module.worker(): KoinDefinition<T> { TODO(USE_KOIN_COMPILER_PLUGIN) }

@KoinDslMarker
public fun <T : ListenableWorker> ScopeDSL.worker(): KoinDefinition<T> { TODO(USE_KOIN_COMPILER_PLUGIN) }
