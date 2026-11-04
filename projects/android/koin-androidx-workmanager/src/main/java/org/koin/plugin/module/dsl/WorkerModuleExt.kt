package org.koin.plugin.module.dsl

import androidx.work.ListenableWorker
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.Definition
import org.koin.core.definition.Kind
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.dsl.ScopeDSL
import kotlin.reflect.KClass


/**
 * Build a worker definition with explicit KClass.
 * Used by the compiler plugin for generated worker definitions.
 *
 * A null [qualifier] is the class itself, matching [org.koin.androidx.workmanager.dsl.worker].
 * The compiler plugin passes null for `worker<T>()` and `@KoinWorker`. [org.koin.androidx.workmanager.factory.KoinWorkerFactory]
 * looks workers up by that class name.
 *
 * Workers are registered as factories and automatically bound to ListenableWorker.
 */
@OptIn(KoinInternalApi::class)
public fun <T : ListenableWorker> Module.buildWorker(kclass: KClass<T>, qualifier: Qualifier? = null, definition: Definition<T>): KoinDefinition<T> {
    return createDefinition(kclass, definition, qualifier = qualifier ?: TypeQualifier(kclass), factoryKind = Kind.Factory, module = this).bind(ListenableWorker::class) as KoinDefinition<T>
}

/**
 * Build a worker definition in a scope with explicit KClass.
 * Used by the compiler plugin for generated scope definitions.
 *
 * A null [qualifier] is the class itself, matching [org.koin.androidx.workmanager.dsl.worker].
 */
@OptIn(KoinInternalApi::class)
public fun <T : ListenableWorker> ScopeDSL.buildWorker(kclass: KClass<T>, qualifier: Qualifier? = null, definition: Definition<T>): KoinDefinition<T> {
    return createDefinition(kclass, definition, qualifier = qualifier ?: TypeQualifier(kclass), scopeQualifier = scopeQualifier, factoryKind = Kind.Factory, module = module).bind(ListenableWorker::class) as KoinDefinition<T>
}
