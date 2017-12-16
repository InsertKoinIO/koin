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
 *
 * @param name - bean name
 * @param clazz - bean class
 * @param isSingleton - is the bean a singleton
 * @param bindTypes - list of assignable types
 * @param definition - bean definition function
 */
data class BeanDefinition<out T>(val name: String = "", val clazz: KClass<*>, val isSingleton: Boolean = true, var bindTypes: List<KClass<*>> = arrayListOf(), val definition: () -> T) {

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

    private fun bindTypes(): String = "(" + bindTypes.map { it.java.canonicalName }.joinToString() + ")"

    override fun toString() = "Bean[name='$name',class=${clazz.java.canonicalName},singleton=$isSingleton,binds~${bindTypes()}]"

    override fun equals(other: Any?): Boolean {
        return if (other is BeanDefinition<*>){
            name == other.name && clazz == other.clazz
        } else false
    }
}