package org.koin.experimental.builder

import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Options
import org.koin.core.definition.createFactory
import org.koin.core.definition.createScoped
import org.koin.core.definition.Kind
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeSet

/**
 * Create a Single definition for given type T
 * @param qualifier
 * @param createOnStart - need to be created at start
 * @param override - allow definition override
 */
inline fun <reified T : Any> ScopeSet<*>.scoped(
        name: Qualifier? = null,
        override: Boolean = false
): BeanDefinition<out Scope, T> = internal(name, override, Kind.Scoped)

/**
 * Create a Factory definition for given type T
 *
 * @param qualifier
 * @param override - allow definition override
 */
inline fun <reified T: Any> ScopeSet<*>.factory(
        name: Qualifier? = null,
        override: Boolean = false
): BeanDefinition<out Scope, T> = internal(name, override, Kind.Factory)

/**
 * Create a Single definition for given type T to modules and cast as R
 * @param qualifier
 * @param createOnStart - need to be created at start
 * @param override - allow definition override
 */
inline fun <reified R : Any, reified T : R> ScopeSet<*>.scopedBy(
        name: Qualifier? = null,
        override: Boolean = false
): BeanDefinition<out Scope, R> = internalBy(name, override, Kind.Scoped)

/**
 * Create a Factory definition for given type T to modules and cast as R
 *
 * @param qualifier
 * @param override - allow definition override
 */
inline fun <S: Scope, reified R : Any, reified T : R> ScopeSet<S>.factoryBy(
        name: Qualifier? = null,
        override: Boolean = false
): BeanDefinition<S, R> {
    return internalBy(name, override, Kind.Factory)
}

@PublishedApi
internal inline fun <S: Scope, reified T: Any> ScopeSet<S>.internal(
        name: Qualifier?,
        override: Boolean,
        kind: Kind
): BeanDefinition<S, T> = internalBy(name, override, kind)

@PublishedApi
internal inline fun <S: Scope, reified R : Any, reified T : R> ScopeSet<S>.internalBy(
        name: Qualifier?,
        override: Boolean,
        kind: Kind
): BeanDefinition<S, R> {
    val beanDefinition = if (kind == Kind.Scoped) {
        this.definitionFactory.createScoped(name, qualifier) { create<T>() as R }
    } else {
        this.definitionFactory.createFactory(name, qualifier) { create<T>() as R }
    }
    declareDefinition(beanDefinition, Options(false, override))
    val added = definitions.add(beanDefinition)
    if (!added) {
        throw DefinitionOverrideException("Can't add definition $beanDefinition as it already exists")
    }
    return beanDefinition
}