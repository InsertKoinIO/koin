/*
 * Copyright 2017-2018 the original author or authors.
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

import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Callbacks
import org.koin.core.definition.OnCloseCallback
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
infix fun <T> BeanDefinition<T>.bind(clazz: KClass<*>): BeanDefinition<T> {
    val copy = copy(secondaryTypes = secondaryTypes + clazz)
    scopeDefinition.remove(this)
    scopeDefinition.save(copy)
    return copy
}

/**
 * Add a compatible type to match for definition
 */
inline fun <reified T> BeanDefinition<*>.bind(): BeanDefinition<*> {
    return bind(T::class)
}

/**
 * Add compatible types to match for definition
 * @param classes
 */
infix fun BeanDefinition<*>.binds(classes: Array<KClass<*>>): BeanDefinition<*> {
    val copy = copy(secondaryTypes = secondaryTypes + classes)
    scopeDefinition.remove(this)
    scopeDefinition.save(copy)
    return copy
}

/**
 * Callback when closing instance
 */
infix fun <T> BeanDefinition<T>.onClose(onClose: OnCloseCallback<T>): BeanDefinition<T> {
    val copy = copy(callbacks = Callbacks(onClose))
    scopeDefinition.remove(this)
    scopeDefinition.save(copy)
    return copy
}