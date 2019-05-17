package org.koin.experimental.builder

import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.DefinitionFactory
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.ScopeSet

/**
 * Declare a ScopeInstance definition
 * @param name
 * @param override
 */
inline fun <reified T : Any> ScopeSet.scoped(
        name: Qualifier? = null
): BeanDefinition<T> {
    val beanDefinition = DefinitionFactory.createScoped(name, qualifier) { create<T>(this) }
    val added = definitions.add(beanDefinition)
    if (!added) {
        throw DefinitionOverrideException("Can't add definition $beanDefinition as it already exists")
    }
    return beanDefinition
}

/**
 * Declare a ScopeInstance definition
 * @param name
 * @param override
 */
inline fun <reified R : Any, reified T : R> ScopeSet.scopedBy(
        name: Qualifier? = null
): BeanDefinition<R> {
    val beanDefinition = DefinitionFactory.createScoped(name, qualifier) { create<T>(this) as R }
    val added = definitions.add(beanDefinition)
    if (!added) {
        throw DefinitionOverrideException("Can't add definition $beanDefinition as it already exists")
    }
    return beanDefinition
}