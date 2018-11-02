package org.koin.core.bean

import kotlin.reflect.KClass

data class BeanDefinition<T>(
    val name: String? = null,
    val primaryType: KClass<*>,
    val definition: Definition<T>
    //TODO Options & Attributes
) {
    lateinit var instance: Instance<T>
}

typealias Definition<T> = () -> T