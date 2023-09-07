/*
 * Copyright 2017-Present the original author or authors.
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
import org.koin.core.definition.KoinDefinition
import org.koin.core.definition.OnCloseCallback
import org.koin.core.definition.indexKey
import org.koin.core.module.OptionDslMarker
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
@OptionDslMarker
infix fun <S : Any> KoinDefinition<out S>.bind(clazz: KClass<S>): KoinDefinition<out S> {
    factory.beanDefinition.secondaryTypes = factory.beanDefinition.secondaryTypes + clazz
    val mapping = indexKey(clazz, factory.beanDefinition.qualifier, factory.beanDefinition.scopeQualifier)
    module.saveMapping(mapping, factory)
    return this
}

/**
 * Add a compatible type to match for definition
 *
 * Type-safety may be checked by "checkModules" from "koin-test" module.
 */
@OptionDslMarker
inline fun <reified S : Any> KoinDefinition<out S>.bind(): KoinDefinition<out S> {
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
@OptionDslMarker
infix fun KoinDefinition<*>.binds(classes: Array<KClass<*>>): KoinDefinition<*> {
    factory.beanDefinition.secondaryTypes += classes
    classes.forEach { clazz ->
        val mapping = indexKey(clazz, factory.beanDefinition.qualifier, factory.beanDefinition.scopeQualifier)
        module.saveMapping(mapping, factory)
    }
    return this
}

/**
 * Callback when closing instance
 */
@OptionDslMarker
infix fun <T> KoinDefinition<T>.onClose(onClose: OnCloseCallback<T>): KoinDefinition<T> {
    factory.beanDefinition.callbacks = Callbacks(onClose)
    return this
}
