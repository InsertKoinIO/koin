@file:Suppress("UNCHECKED_CAST")

package org.koin.core.bean

import org.koin.core.KoinApplication

interface Instance<T> {

    fun <T> get(): T

    fun <T> create(beanDefinition: BeanDefinition<*>): T {
        KoinApplication.log("[Koin] create instance")
        return beanDefinition.definition() as T
    }

    fun release()
}