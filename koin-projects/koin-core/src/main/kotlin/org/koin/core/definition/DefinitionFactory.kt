package org.koin.core.definition

import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.DefaultScope
import org.koin.core.scope.ObjectScope
import org.koin.core.scope.RootScope
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeSet
import kotlin.reflect.KClass

/**
 * Contains extension functions for ScopeSet and Scope for creating BeanDefinitions of kind
 * factory and scoped.
 *
 * @author Arnaud Giuliani
 * @author Andreas Schattney
 */

inline fun <S: Scope, reified T> ScopeSet<S>.createScoped(
        qualifier: Qualifier? = null,
        scopeName: Qualifier? = null,
        noinline definition: Definition<S, T>
): BeanDefinition<S, T> {
    return createScopedWithType(qualifier, scopeName, T::class, definition)
}

inline fun <S: Scope, reified T> ScopeSet<S>.createFactory(
        qualifier: Qualifier? = null,
        scopeName: Qualifier? = null,
        noinline definition: Definition<S, T>
): BeanDefinition<S, T> {
    return createFactoryWithType(qualifier, scopeName, T::class, definition)
}

inline fun <reified T> createScoped(
        qualifier: Qualifier? = null,
        scopeName: Qualifier? = null,
        noinline definition: Definition<RootScope, T>
): BeanDefinition<RootScope, T> {
    return createScopedWithType(qualifier, scopeName, T::class, definition)
}

inline fun <reified T> createFactory(
        qualifier: Qualifier? = null,
        scopeName: Qualifier? = null,
        noinline definition: Definition<RootScope, T>
): BeanDefinition<RootScope, T> {
    return createFactoryWithType(qualifier, scopeName, T::class, definition)
}

inline fun <S: Scope, reified T> S.createScoped(
        qualifier: Qualifier? = null,
        scopeName: Qualifier? = null,
        noinline definition: Definition<S, T>
): BeanDefinition<S, T> {
    return createScopedWithType(qualifier, scopeName, T::class, definition)
}

inline fun <S: Scope, reified T> S.createFactory(
        qualifier: Qualifier? = null,
        scopeName: Qualifier? = null,
        noinline definition: Definition<S, T>
): BeanDefinition<S, T> {
    return createFactoryWithType(qualifier, scopeName, T::class, definition)
}

fun <S: Scope, T> createScopedWithType(
        qualifier: Qualifier? = null,
        scopeName: Qualifier? = null,
        clazz: KClass<*>,
        definition: Definition<S, T>
) = createDefinition(qualifier, definition, Kind.Scoped, scopeName, clazz)

fun <S: Scope, T> createFactoryWithType(
        qualifier: Qualifier? = null,
        scopeName: Qualifier? = null,
        clazz: KClass<*>,
        definition: Definition<S, T>
): BeanDefinition<S, T> {
    return createDefinition(qualifier, definition, Kind.Factory, scopeName, clazz)
}

private fun <S: Scope, T> createDefinition(
        qualifier: Qualifier?,
        definition: Definition<S, T>,
        kind: Kind,
        scopeName: Qualifier?,
        clazz: KClass<*>
): BeanDefinition<S, T> {
    return BeanDefinition(qualifier, scopeName, clazz, kind, definition)
}