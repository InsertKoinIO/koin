package org.koin.plugin.module.dsl

import org.koin.core.definition.KoinDefinition
import org.koin.core.module.KoinDslMarker
import org.koin.core.module.Module
import org.koin.dsl.ScopeDSL

@KoinDslMarker
public fun <T> org.koin.core.module.Module.single(): KoinDefinition<T> { USE_KOIN_COMPILER_PLUGIN("single<T>()") }

@KoinDslMarker
public fun <T> org.koin.core.module.Module.factory(): KoinDefinition<T> { USE_KOIN_COMPILER_PLUGIN("factory<T>()") }

@KoinDslMarker
public fun <T> ScopeDSL.scoped(): KoinDefinition<T> { USE_KOIN_COMPILER_PLUGIN("ScopeDSL.scoped<T>()") }

@KoinDslMarker
public fun <T> ScopeDSL.factory(): KoinDefinition<T> { USE_KOIN_COMPILER_PLUGIN("ScopeDSL.factory<T>()") }