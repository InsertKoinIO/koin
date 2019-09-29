package org.koin.experimental.builder

import org.koin.core.definition.*
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
): BeanDefinition<S, T> {
    val beanDefinition = this.factory.createScoped(name, qualifier) { create<T>() }
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
): BeanDefinition<S, T> {
    val beanDefinition = this.factory.createFactory(name, qualifier) { create<T>() }
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
): BeanDefinition<S, R> {
    val beanDefinition = this.factory.createScoped(name, qualifier) { create<T>() as R }
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
): BeanDefinition<S, R> {
    val beanDefinition = this.factory.createFactory(name, qualifier) { create<T>() as R }
    declareDefinition(beanDefinition, Options(false, override))
    val added = definitions.add(beanDefinition)
    if (!added) {
        throw DefinitionOverrideException("Can't add definition $beanDefinition as it already exists")
    }
    return beanDefinition
}