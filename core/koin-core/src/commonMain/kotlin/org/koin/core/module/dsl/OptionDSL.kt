@file:OptIn(KoinInternalApi::class)

package org.koin.core.module.dsl

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Callbacks
import org.koin.core.definition.OnCloseCallback
import org.koin.core.instance.InstanceFactory
import org.koin.core.instance.SingleInstanceFactory
import org.koin.core.module.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.qualifier.StringQualifier
import org.koin.core.qualifier.TypeQualifier
import kotlin.reflect.KClass

/**
 * Koin DSL Options
 *
 * @author Arnaud Giuliani
 */

infix fun <T> KoinDefinition<T>.withOptions(
    options: BeanDefinition<T>.() -> Unit
): KoinDefinition<T> {
    val factory = second
    val module = first
    val def = second.beanDefinition
    val primary = def.qualifier
    def.also(options)
    if (def.qualifier != primary){
        module.indexPrimaryType(factory)
    }
    module.indexSecondaryTypes(factory)
    if (def._createdAtStart && factory is SingleInstanceFactory<*>) {
        module.prepareForCreationAtStart(factory)
    }
    return this
}

@KoinInternalApi
inline fun <reified R> Module.setupInstance(
    factory: InstanceFactory<R>,
    options: BeanDefinition<R>.() -> Unit
): KoinDefinition<R> {
    val def = factory.beanDefinition
    val koinDef = Pair(this, factory)
    def.also(options)
    indexPrimaryType(factory)
    indexSecondaryTypes(factory)
    if (def._createdAtStart && factory is SingleInstanceFactory<*>) {
        prepareForCreationAtStart(factory)
    }
    return koinDef
}

fun BeanDefinition<*>.named(name: String) {
    qualifier = StringQualifier(name)
}

inline fun <reified T> BeanDefinition<*>.named() {
    qualifier = TypeQualifier(T::class)
}

inline fun <reified T> BeanDefinition<out T>.bind() {
    secondaryTypes += T::class
}

fun BeanDefinition<*>.binds(classes: List<KClass<*>>) {
    secondaryTypes += classes
}

fun BeanDefinition<*>.createdAtStart() {
    _createdAtStart = true
}

fun <T> BeanDefinition<T>.onClose(onClose: OnCloseCallback<T>) {
    callbacks = Callbacks(onClose)
}