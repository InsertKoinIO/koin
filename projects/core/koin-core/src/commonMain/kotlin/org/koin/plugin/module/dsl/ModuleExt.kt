@file:OptIn(KoinInternalApi::class)

package org.koin.plugin.module.dsl

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definition
import org.koin.core.definition.Kind
import org.koin.core.definition.KoinDefinition
import org.koin.core.instance.FactoryInstanceFactory
import org.koin.core.instance.ScopedInstanceFactory
import org.koin.core.instance.SingleInstanceFactory
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.qualifier._q
import org.koin.dsl.ScopeDSL
import kotlin.reflect.KClass
import org.koin.core.module.Module

// Unlock from Koin
private const val ROOT_SCOPE_ID = "_root_"

/**
 * Root scope qualifier.
 */
public val rootScopeQualifier: Qualifier = _q(ROOT_SCOPE_ID)

/**
 * Build a singleton definition with explicit KClass.
 * Used by the compiler plugin for generated module definitions.
 */
public fun <T : Any> Module.buildSingle(
    kclass: KClass<T>,
    qualifier: Qualifier? = null,
    definition: Definition<T>,
    createdAtStart: Boolean = false
): KoinDefinition<T> {
    return createDefinition(kclass, definition, qualifier = qualifier, factoryKind = Kind.Singleton, module = this, createdAtStart = createdAtStart)
}

/**
 * Build a factory definition with explicit KClass.
 * Used by the compiler plugin for generated module definitions.
 */
public fun <T : Any> Module.buildFactory(kclass: KClass<T>, qualifier: Qualifier? = null, definition: Definition<T>): KoinDefinition<T> {
    return createDefinition(kclass, definition, qualifier = qualifier, factoryKind = Kind.Factory, module = this)
}
/**
 * Build a scoped definition with explicit KClass.
 * Used by the compiler plugin for generated scope definitions.
 */
public fun <T : Any> ScopeDSL.buildScoped(kclass: KClass<T>, qualifier: Qualifier? = null, definition: Definition<T>): KoinDefinition<T> {
    return createDefinition(kclass, definition, qualifier = qualifier, scopeQualifier = scopeQualifier, factoryKind = Kind.Scoped, module = module)
}

/**
 * Build a factory definition in a scope with explicit KClass.
 * Used by the compiler plugin for generated scope definitions.
 */
public fun <T : Any> ScopeDSL.buildFactory(kclass: KClass<T>, qualifier: Qualifier? = null, definition: Definition<T>): KoinDefinition<T> {
    return createDefinition(kclass, definition, qualifier = qualifier, scopeQualifier = scopeQualifier, factoryKind = Kind.Factory, module = module)
}

/**
 * Define a scope section with a qualifier.
 * Non-inline version for compiler plugin use.
 */
public fun Module.scope(qualifier: Qualifier, scopeSet: ScopeDSL.() -> Unit) {
    ScopeDSL(qualifier, this).apply(scopeSet)
}

/**
 * Include another module's definitions.
 * Non-inline version for compiler plugin use.
 */
public fun Module.includes(module: Module) {
    this.includes(listOf(module))
}

/**
 * Create a TypeQualifier from a KClass.
 * Non-inline version for compiler plugin use.
 */
public fun <T : Any> typeQualifier(kclass: KClass<T>): Qualifier = TypeQualifier(kclass)

/**
 * Bind additional type to definition.
 * Non-infix version for compiler plugin use.
 */
@OptIn(KoinInternalApi::class)
public fun <S : Any> KoinDefinition<*>.bind(clazz: KClass<S>): KoinDefinition<*> {
    factory.beanDefinition.secondaryTypes = factory.beanDefinition.secondaryTypes + clazz
    module.indexSecondaryTypes(factory)
    return this
}

@KoinInternalApi
fun <T : Any> createDefinition(
    kclass: KClass<T>,
    definition: Definition<T>,
    qualifier: Qualifier? = null,
    scopeQualifier: Qualifier = rootScopeQualifier,
    factoryKind: Kind,
    module: Module,
    createdAtStart: Boolean = false
): KoinDefinition<T> {
    // Explicitly pass secondaryTypes for binary compatibility with Koin 4.1.x and 4.2.x
    val def = BeanDefinition(
        scopeQualifier,
        kclass,
        qualifier,
        definition,
        factoryKind,
        emptyList()  // secondaryTypes - explicit to avoid default parameter bytecode differences
    )
    val factory = when (factoryKind) {
        Kind.Singleton -> SingleInstanceFactory(def)
        Kind.Factory -> FactoryInstanceFactory(def)
        Kind.Scoped -> ScopedInstanceFactory(def)
    }
    module.indexPrimaryType(factory)

    // Handle createdAtStart for singletons
    if (createdAtStart && factoryKind == Kind.Singleton && factory is SingleInstanceFactory<*>) {
        module.eagerInstances.add(factory)
    }

    return KoinDefinition(module, factory)
}

// ============================================================================
// Scope Archetype Functions
// ============================================================================

/**
 * ViewModel scope archetype.
 * Non-inline version for compiler plugin use.
 */
public fun Module.viewModelScope(scopeSet: ScopeDSL.() -> Unit) {
    ScopeDSL(_q("org.koin.android.scope.AndroidViewModelScope"), this).apply(scopeSet)
}

/**
 * Activity scope archetype.
 * Non-inline version for compiler plugin use.
 */
public fun Module.activityScope(scopeSet: ScopeDSL.() -> Unit) {
    ScopeDSL(_q("org.koin.android.scope.AndroidActivityScope"), this).apply(scopeSet)
}

/**
 * Activity retained scope archetype.
 * Non-inline version for compiler plugin use.
 */
public fun Module.activityRetainedScope(scopeSet: ScopeDSL.() -> Unit) {
    ScopeDSL(_q("org.koin.android.scope.AndroidActivityRetainedScope"), this).apply(scopeSet)
}

/**
 * Fragment scope archetype.
 * Non-inline version for compiler plugin use.
 */
public fun Module.fragmentScope(scopeSet: ScopeDSL.() -> Unit) {
    ScopeDSL(_q("org.koin.android.scope.AndroidFragmentScope"), this).apply(scopeSet)
}
