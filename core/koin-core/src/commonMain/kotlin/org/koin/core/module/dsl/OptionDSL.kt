@file:OptIn(KoinInternalApi::class)

package org.koin.core.module.dsl

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Callbacks
import org.koin.core.definition.KoinDefinition
import org.koin.core.definition.OnCloseCallback
import org.koin.core.instance.SingleInstanceFactory
import org.koin.core.module.OptionDslMarker
import org.koin.core.qualifier.StringQualifier
import org.koin.core.qualifier.TypeQualifier
import kotlin.reflect.KClass

typealias DefinitionOptions<T> = BeanDefinition<T>.() -> Unit

/**
 * Koin DSL Options
 *
 * @author Arnaud Giuliani
 */
@OptionDslMarker
inline infix fun <T> KoinDefinition<T>.withOptions(
    options: DefinitionOptions<T>
): KoinDefinition<T> {
    val def = factory.beanDefinition
    val primary = def.qualifier
    def.also(options)
    if (def.qualifier != primary) {
        module.indexPrimaryType(factory)
    }
    if (def.secondaryTypes.isNotEmpty()) {
        module.indexSecondaryTypes(factory)
    }
    if (def._createdAtStart && factory is SingleInstanceFactory<*>) {
        module.prepareForCreationAtStart(factory)
    }
    return this
}

fun <T> KoinDefinition<T>.onOptions(
    options: DefinitionOptions<T>? = null
): KoinDefinition<T> {
    if (options != null) {
        withOptions(options)
    }
    return this
}

@OptionDslMarker
fun BeanDefinition<*>.named(name: String) {
    qualifier = StringQualifier(name)
}

@OptionDslMarker
inline fun <reified T> BeanDefinition<*>.named() {
    qualifier = TypeQualifier(T::class)
}

@OptionDslMarker
inline fun <reified T> BeanDefinition<out T>.bind() {
    secondaryTypes += T::class
}

@OptionDslMarker
fun BeanDefinition<*>.binds(classes: List<KClass<*>>) {
    secondaryTypes += classes
}

@OptionDslMarker
fun BeanDefinition<*>.createdAtStart() {
    _createdAtStart = true
}

@OptionDslMarker
fun <T> BeanDefinition<T>.onClose(onClose: OnCloseCallback<T>) {
    callbacks = Callbacks(onClose)
}
