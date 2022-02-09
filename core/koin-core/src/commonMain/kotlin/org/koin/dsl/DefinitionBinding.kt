/*
 * Copyright 2017-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.koin.dsl

import org.koin.core.definition.Callbacks
import org.koin.core.definition.OnCloseCallback
import org.koin.core.definition.indexKey
import org.koin.core.instance.InstanceFactory
import org.koin.core.instance.SingleInstanceFactory
import org.koin.core.module.KoinDefinition
import org.koin.core.module.Module
import kotlin.reflect.KClass

/**
 * BeanDefinition DSL specific functions
 *
 * @author Arnaud Giuliani
 */

/**
 * Add a compatible type to match for definition
 * @param clazz
 */
infix fun <S : Any, P : S> KoinDefinition<P>.bind(clazz: KClass<S>): KoinDefinition<P> {
    second.beanDefinition.secondaryTypes = second.beanDefinition.secondaryTypes + clazz
    val mapping = indexKey(clazz,second.beanDefinition.qualifier,second.beanDefinition.scopeQualifier)
    first.saveMapping(mapping,second,allowOverride = true)
    return this
}

/**
 * Add a compatible type to match for definition
 *
 * Type-safety may be checked by "checkModules" from "koin-test" module.
 */
inline fun <reified S : Any, P : S> KoinDefinition<P>.bind(): Pair<Module, InstanceFactory<P>> {
    bind(clazz = S::class)
    return this
}

/**
 * Add compatible types to match for definition
 *
 * Type-safety may be checked by "checkModules" from "koin-test" module.
 *
 * @param classes
 */
infix fun <T> KoinDefinition<T>.binds(classes: Array<KClass<*>>): Pair<Module, InstanceFactory<T>> {
    classes.forEach { clazz ->
        bind(clazz = clazz)
    }
    return this
}

/**
 * Callback when closing instance
 */
infix fun <T> KoinDefinition<T>.onClose(onClose: OnCloseCallback<T>): Pair<Module, InstanceFactory<T>> {
    second.beanDefinition.callbacks = Callbacks(onClose)
    return this
}