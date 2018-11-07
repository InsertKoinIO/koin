package org.koin.dsl

import org.koin.core.bean.BeanDefinition
import kotlin.reflect.KClass

infix fun <T> BeanDefinition<T>.bind(clazz: KClass<*>) {
    this.secondaryTypes.add(clazz)
}

infix fun BeanDefinition<*>.binds(classes: Array<KClass<*>>) {
    this.secondaryTypes.addAll(classes)
}
