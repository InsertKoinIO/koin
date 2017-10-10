package org.koin.core.bean

import kotlin.reflect.KClass

/**
 * Bean definition
 * @author - Arnaud GIULIANI
 *
 * Gather type of T
 * defined by lazy/function
 * has a type (clazz)
 * has a BeanType : default singleton
 * has a name, if specified
 */
data class BeanDefinition<out T>(val name: String = "", val clazz: KClass<*>, val isSingleton: Boolean, var bindTypes: List<KClass<*>> = arrayListOf(), val definition: () -> T) {

    /**
     * Add a compatible type to current bounded definition
     */
    infix fun bind(clazz: KClass<*>): BeanDefinition<*> {
        bindTypes += clazz
        return this
    }

    /**
     * Bean definition is not a singleton, but a factory
     */
    fun isNotASingleton() = !isSingleton

    override fun toString() = "Bean[name='$name',class=${clazz.java.simpleName},singleton=$isSingleton,binds~${bindTypes.size}]"
}