@file:OptIn(KoinInternalApi::class)

package org.koin.plugin.module.dsl

import androidx.lifecycle.ViewModel
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.Definition
import org.koin.core.definition.Kind
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.ScopeDSL
import kotlin.reflect.KClass

/**
 * Build a ViewModel definition with explicit KClass.
 * Used by the compiler plugin for generated module definitions.
 * Not bound to [ViewModel]: a binding shared by every ViewModel collides across modules (#2474).
 */
public fun <T : ViewModel> Module.buildViewModel(kclass: KClass<T>, qualifier: Qualifier? = null, definition: Definition<T>): KoinDefinition<T> {
    return createDefinition(kclass, definition, qualifier = qualifier, factoryKind = Kind.Factory, module = this)
}

/**
 * Build a ViewModel definition in a scope with explicit KClass.
 * Used by the compiler plugin for generated scope definitions.
 * Not bound to [ViewModel]: a binding shared by every ViewModel collides across modules (#2474).
 */
public fun <T : ViewModel> ScopeDSL.buildViewModel(kclass: KClass<T>, qualifier: Qualifier? = null, definition: Definition<T>): KoinDefinition<T> {
    return createDefinition(kclass, definition, qualifier = qualifier, scopeQualifier = scopeQualifier, factoryKind = Kind.Factory, module = module)
}
