@file:OptIn(KoinInternalApi::class, KoinInternalApi::class)

package org.koin.core.module

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definition
import org.koin.core.definition.Kind
import org.koin.core.definition.KoinDefinition
import org.koin.core.instance.FactoryInstanceFactory
import org.koin.core.instance.SingleInstanceFactory
import org.koin.core.qualifier.Qualifier
import org.koin.core.registry.ScopeRegistry
import kotlin.reflect.KClass

/**
 * Module extension for Native to allow use of Module API without inlined KClass Type
 *
 * @author Arnaud Giuliani
 */

internal fun <T : Any> Module.createDefinition(
    kind: Kind = Kind.Singleton,
    kClass: KClass<T>,
    qualifier: Qualifier? = null,
    definition: Definition<T>,
    secondaryTypes: List<KClass<*>> = emptyList(),
    scopeQualifier: Qualifier = ScopeRegistry.rootScopeQualifier,
): BeanDefinition<T> {
    return BeanDefinition(
        scopeQualifier,
        kClass,
        qualifier,
        definition,
        kind,
        secondaryTypes = secondaryTypes,
    )
}

/**
 * Create a factory
 */
fun <T : Any> Module.factory(
    kClass: KClass<T>,
    qualifier: Qualifier? = null,
    definition: Definition<T>,
    scopeQualifier: Qualifier = ScopeRegistry.rootScopeQualifier,
): KoinDefinition<T> {
    val def = createDefinition(Kind.Factory, kClass, qualifier, definition, scopeQualifier = scopeQualifier)
    val factory = FactoryInstanceFactory(def)
    indexPrimaryType(factory)
    return KoinDefinition(this, factory)
}

/**
 * Create a Single
 */
fun <T : Any> Module.single(
    kClass: KClass<T>,
    qualifier: Qualifier? = null,
    definition: Definition<T>,
    createdAtStart: Boolean = false,
    scopeQualifier: Qualifier = ScopeRegistry.rootScopeQualifier,
): KoinDefinition<T> {
    val def = createDefinition(Kind.Singleton, kClass, qualifier, definition, scopeQualifier = scopeQualifier)
    val factory = SingleInstanceFactory(def)
    indexPrimaryType(factory)
    if (createdAtStart) {
        prepareForCreationAtStart(factory)
    }
    return KoinDefinition(this, factory)
}
