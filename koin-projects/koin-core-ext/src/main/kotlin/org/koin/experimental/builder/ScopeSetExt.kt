package org.koin.experimental.builder

import org.koin.core.definition.BeanDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.ScopeDSL

/**
 * Create a Single definition for given type T
 * @param qualifier
 * @param override - allow definition override
 */
inline fun <reified T : Any> ScopeDSL.scoped(
        qualifier: Qualifier? = null,
        override: Boolean = false
): BeanDefinition<T> {
    return scoped(qualifier, override) { create<T>() }
}

/**
 * Create a Factory definition for given type T
 *
 * @param qualifier
 * @param override - allow definition override
 */
inline fun <reified T : Any> ScopeDSL.factory(
        qualifier: Qualifier? = null,
        override: Boolean = false
): BeanDefinition<T> {
    return factory(qualifier, override) { create<T>() }
}

/**
 * Create a Single definition for given type T to modules and cast as R
 * @param qualifier
 * @param override - allow definition override
 */
inline fun <reified R : Any, reified T : R> ScopeDSL.scopedBy(
        qualifier: Qualifier? = null,
        override: Boolean = false
): BeanDefinition<R> {
    return scoped(qualifier, override) { create<T>() as R }
}

/**
 * Create a Factory definition for given type T to modules and cast as R
 *
 * @param qualifier
 * @param override - allow definition override
 */
inline fun <reified R : Any, reified T : R> ScopeDSL.factoryBy(
        qualifier: Qualifier? = null,
        override: Boolean = false
): BeanDefinition<R> {
    return factory(qualifier, override) { create<T>() as R }
}