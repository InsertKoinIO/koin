package org.koin.dsl.context

import org.koin.KoinContext
import org.koin.bean.BeanDefinition
import kotlin.reflect.KClass

/**
 * Koin Context
 * Define dependencies & properties for actual context
 * @author - Arnaud GIULIANI
 */
class Context(val koinContext: KoinContext) {

    val provided = arrayListOf<BeanDefinition<*>>()

    /*
     * Dependency declaration
     */

    /**
     * Declared context scope
     */
    var contextScope: Scope? = null

    /**
     * declare a Context scope
     */
    fun scope(definition: () -> KClass<*>) {
        contextScope = Scope(definition())
    }

    /**
     * Provide a bean definition & empty name
     */
    inline fun <reified T : Any> provide(noinline definition: () -> T): BeanDefinition<T> = provide("", definition)

    /**
     * Provide a bean definition with a name
     */
    inline fun <reified T : Any> provide(name: String, noinline definition: () -> T): BeanDefinition<T> {
        val beanDefinition = BeanDefinition(definition, T::class, contextScope ?: Scope.root(), name = name)
        provided += beanDefinition
        return beanDefinition
    }

    /*
        Dependency resolvers
     */

    /**
     * Resolve a component
     */
    inline fun <reified T : Any> get(): T = koinContext.resolveByClass()

    /**
     * Resolve a component
     */
    inline fun <reified T : Any> get(name: String): T = koinContext.resolveByName<T>(name)

    /**
     * Retrieve a property
     */
    inline fun <reified T> getProperty(key: String): T = koinContext.propertyResolver.getProperty(key)
}