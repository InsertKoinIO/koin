package org.koin.experimental.builder

import org.koin.core.definition.BeanDefinition
import org.koin.core.instance.InstanceFactory
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.ScopeDSL

/**
 * Create a Single definition for given type T
 * @param qualifier
 * @param override - allow definition override
 */
inline fun <reified R : Any> ScopeDSL.scoped(
        qualifier: Qualifier? = null
): Pair<Module, InstanceFactory<R>> {
    return scoped(qualifier) { create<R>() }
}

/**
 * Create a Factory definition for given type T
 *
 * @param qualifier
 * @param override - allow definition override
 */
inline fun <reified R : Any> ScopeDSL.factory(
        qualifier: Qualifier? = null
): Pair<Module, InstanceFactory<R>> {
    return factory(qualifier) { create<R>() }
}

/**
 * Create a Single definition for given type T to modules and cast as R
 * @param qualifier
 * @param override - allow definition override
 */
inline fun <reified R : Any, reified T : R> ScopeDSL.scopedBy(
        qualifier: Qualifier? = null,
):Pair<Module, InstanceFactory<R>> {
    return scoped(qualifier) { create<T>() as R }
}

/**
 * Create a Factory definition for given type T to modules and cast as R
 *
 * @param qualifier
 * @param override - allow definition override
 */
inline fun <reified R : Any, reified T : R> ScopeDSL.factoryBy(
        qualifier: Qualifier? = null,
): Pair<Module, InstanceFactory<R>> {
    return factory(qualifier) { create<T>() as R }
}