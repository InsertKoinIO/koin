package org.koin.dsl.ext

import org.koin.dsl.context.ModuleDefinition
import org.koin.dsl.definition.BeanDefinition


inline fun <reified T : Any, reified R : Any> ModuleDefinition.single(
    name: String = "",
    createOnStart: Boolean = false,
    override: Boolean = false
): BeanDefinition<*> {
    return single(name, createOnStart, override) { build<T>() as R }
}

inline fun <reified T : Any, reified R : Any> ModuleDefinition.factory(
    name: String = "",
    override: Boolean = false
): BeanDefinition<*> {
    return factory(name, override) { build<T>() as R }
}