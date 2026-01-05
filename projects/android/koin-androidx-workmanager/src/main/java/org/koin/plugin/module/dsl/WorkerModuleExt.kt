package org.koin.plugin.module.dsl

import androidx.work.ListenableWorker
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definition
import org.koin.core.definition.Kind
import org.koin.core.definition.KoinDefinition
import org.koin.core.instance.FactoryInstanceFactory
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.qualifier._q
import org.koin.dsl.ScopeDSL
import kotlin.reflect.KClass

// Unlock from Koin
private const val ROOT_SCOPE_ID = "_root_"

/**
 * Root scope qualifier.
 */
public val workerRootScopeQualifier: Qualifier = _q(ROOT_SCOPE_ID)

/**
 * Build a worker definition with explicit KClass.
 * Used by the compiler plugin for generated worker definitions.
 *
 * Workers are registered as factories and automatically bound to ListenableWorker.
 */
public fun <T : ListenableWorker> Module.buildWorker(kclass: KClass<T>, qualifier: Qualifier? = null, definition: Definition<T>): KoinDefinition<T> {
    return createWorkerDefinition(kclass, definition, qualifier = qualifier, module = this)
}

/**
 * Build a worker definition in a scope with explicit KClass.
 * Used by the compiler plugin for generated scope definitions.
 */
public fun <T : ListenableWorker> ScopeDSL.buildWorker(kclass: KClass<T>, qualifier: Qualifier? = null, definition: Definition<T>): KoinDefinition<T> {
    return createWorkerDefinition(kclass, definition, qualifier = qualifier, scopeQualifier = scopeQualifier, module = module)
}

/**
 * Create a TypeQualifier from a KClass.
 * Non-inline version for compiler plugin use.
 */
public fun <T : Any> workerTypeQualifier(kclass: KClass<T>): Qualifier = TypeQualifier(kclass)

@OptIn(KoinInternalApi::class)
private fun <T : ListenableWorker> createWorkerDefinition(
    kclass: KClass<T>,
    definition: Definition<T>,
    qualifier: Qualifier? = null,
    scopeQualifier: Qualifier = workerRootScopeQualifier,
    module: Module,
): KoinDefinition<T> {
    // Workers are always factories
    // Explicitly pass secondaryTypes for binary compatibility with Koin 4.1.x and 4.2.x
    val def = BeanDefinition(
        scopeQualifier,
        kclass,
        qualifier,
        definition,
        Kind.Factory,
        emptyList()  // secondaryTypes - explicit to avoid default parameter bytecode differences
    )
    val factory = FactoryInstanceFactory(def)
    module.indexPrimaryType(factory)

    // Auto-bind to ListenableWorker for WorkManager integration
    val koinDef = KoinDefinition(module, factory)
    factory.beanDefinition.secondaryTypes = factory.beanDefinition.secondaryTypes + ListenableWorker::class
    module.indexSecondaryTypes(factory)

    return koinDef
}
