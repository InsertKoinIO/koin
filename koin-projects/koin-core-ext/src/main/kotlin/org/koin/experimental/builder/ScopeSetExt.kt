package org.koin.experimental.builder

import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.DefinitionFactory
import org.koin.core.definition.Options
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
inline fun <S: Scope, reified T : Any> ScopeSet<S>.scoped(
        name: Qualifier? = null,
        override: Boolean = false
): BeanDefinition<T> {
    val beanDefinition = DefinitionFactory.createScoped<S, T>(name, qualifier) { create<T>() }
    declareDefinition(beanDefinition, Options(false, override))
    val added = definitions.add(beanDefinition)
    if (!added) {
        throw DefinitionOverrideException("Can't add definition $beanDefinition as it already exists")
    }
    return beanDefinition
}

/**
 * Create a Factory definition for given type T
 *
 * @param qualifier
 * @param override - allow definition override
 */
inline fun <S: Scope, reified T : Any> ScopeSet<S>.factory(
        name: Qualifier? = null,
        override: Boolean = false
): BeanDefinition<T> {
    val beanDefinition = DefinitionFactory.createFactory<S, T>(name, qualifier) { create<T>() }
    declareDefinition(beanDefinition, Options(false, override))
    val added = definitions.add(beanDefinition)
    if (!added) {
        throw DefinitionOverrideException("Can't add definition $beanDefinition as it already exists")
    }
    return beanDefinition
}

/**
 * Create a Single definition for given type T to modules and cast as R
 * @param qualifier
 * @param createOnStart - need to be created at start
 * @param override - allow definition override
 */
inline fun <S: Scope, reified R : Any, reified T : R> ScopeSet<S>.scopedBy(
        name: Qualifier? = null,
        override: Boolean = false
): BeanDefinition<R> {
    val beanDefinition = DefinitionFactory.createScoped<S, R>(name, qualifier) { create<T>() }
    declareDefinition(beanDefinition, Options(false, override))
    val added = definitions.add(beanDefinition)
    if (!added) {
        throw DefinitionOverrideException("Can't add definition $beanDefinition as it already exists")
    }
    return beanDefinition
}

/**
 * Create a Factory definition for given type T to modules and cast as R
 *
 * @param qualifier
 * @param override - allow definition override
 */
inline fun <S: Scope, reified R : Any, reified T : R> ScopeSet<S>.factoryBy(
        name: Qualifier? = null,
        override: Boolean = false
): BeanDefinition<R> {
    val beanDefinition = DefinitionFactory.createFactory<S, R>(name, qualifier) { create<T>() }
    declareDefinition(beanDefinition, Options(false, override))
    val added = definitions.add(beanDefinition)
    if (!added) {
        throw DefinitionOverrideException("Can't add definition $beanDefinition as it already exists")
    }
    return beanDefinition
}