package org.koin.core.bean

import kotlin.reflect.KClass

//TODO Options & Attributes
data class BeanDefinition<T>(
    val name: String? = null,
    val primaryType: KClass<*>
) {
    lateinit var definition: Definition<T>
    lateinit var instance: Instance<T>
}

typealias Definition<T> = () -> T