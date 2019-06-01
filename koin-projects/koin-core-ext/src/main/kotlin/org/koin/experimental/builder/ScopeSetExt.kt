package org.koin.experimental.builder

import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.DefinitionFactory
import org.koin.core.definition.Options
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.ScopeSet

/**
 * Create a Single definition for given type T
 * @param qualifier
 * @param createOnStart - need to be created at start
 * @param override - allow definition override
 */
inline fun <reified T : Any> ScopeSet.scoped(
        name: Qualifier? = null,
        override: Boolean = false
): BeanDefinition<T> {
    val beanDefinition = DefinitionFactory.createScoped(name, qualifier) { create<T>(this) }
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
inline fun <reified T : Any> ScopeSet.factory(
        name: Qualifier? = null,
        override: Boolean = false
): BeanDefinition<T> {
    val beanDefinition = DefinitionFactory.createFactory(name, qualifier) { create<T>(this) }
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
inline fun <reified R : Any, reified T : R> ScopeSet.scopedBy(
        name: Qualifier? = null,
        override: Boolean = false
): BeanDefinition<R> {
    val beanDefinition = DefinitionFactory.createScoped(name, qualifier) { create<T>(this) as R }
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
inline fun <reified R : Any, reified T : R> ScopeSet.factoryBy(
        name: Qualifier? = null,
        override: Boolean = false
): BeanDefinition<R> {
    val beanDefinition = DefinitionFactory.createFactory(name, qualifier) { create<T>(this) as R }
    declareDefinition(beanDefinition, Options(false, override))
    val added = definitions.add(beanDefinition)
    if (!added) {
        throw DefinitionOverrideException("Can't add definition $beanDefinition as it already exists")
    }
    return beanDefinition
}