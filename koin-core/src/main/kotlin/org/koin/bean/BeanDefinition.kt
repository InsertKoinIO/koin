package org.koin.bean

import org.koin.dsl.context.Scope
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
data class BeanDefinition<out T>(val definition: () -> T, val clazz: KClass<*>, val scope: Scope = Scope.root(), var bindTypes: List<KClass<*>> = arrayListOf(), val name: String = "") {

    /**
     * Add a compatible type to current bounded definition
     */
    infix fun bind(bind: () -> KClass<*>): BeanDefinition<T> {
        bindTypes += bind()
        return this
    }

    override fun toString() = "Bean[name=$name,class=${clazz.java.simpleName},binds=${bindTypes.size}]"
}