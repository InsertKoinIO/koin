@file:Suppress("UNCHECKED_CAST")

package org.koin.core.instance

import org.koin.core.KoinApplication
import org.koin.core.bean.BeanDefinition
import org.koin.core.error.InstanceCreationException

interface Instance<T> {

    fun <T> get(): T

    fun <T> create(beanDefinition: BeanDefinition<*>): T {
        KoinApplication.log("[Koin] create instance ~ $beanDefinition")
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