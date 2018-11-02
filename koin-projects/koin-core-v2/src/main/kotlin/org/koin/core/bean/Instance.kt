@file:Suppress("UNCHECKED_CAST")

package org.koin.core.bean

import org.koin.core.KoinApplication
import org.koin.core.error.InstanceCreationException

interface Instance<T> {

    fun <T> get(): T

    fun <T> create(beanDefinition: BeanDefinition<*>): T {
        KoinApplication.log("[Koin] create instance")
        try {
            return beanDefinition.definition() as T
        } catch (e: Exception) {
            //TODO Format error
            e.printStackTrace()
            throw InstanceCreationException("Error while creating instance for $beanDefinition")
        }
    }

    fun release()
}