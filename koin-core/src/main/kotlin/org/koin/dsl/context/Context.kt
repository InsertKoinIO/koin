package org.koin.dsl.context

import org.koin.KoinContext
import org.koin.bean.BeanDefinition
import org.koin.error.InstanceNotFoundException
import org.koin.error.MissingPropertyException
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
    inline fun <reified T : Any> get(): T = getOrNull() ?: throw InstanceNotFoundException("no bean instance for ${T::class}")

    /**
     * Resolve a component
     */
    inline fun <reified T : Any> get(name: String): T = getOrNull(name) ?: throw InstanceNotFoundException("no bean instance for ${T::class}")

    /**
     * Safely resolve a component (can be null)
     */
    inline fun <reified T : Any> getOrNull(name: String): T? = koinContext.resolveByName<T>(name)

    /**
     * Safely resolve a component (can be null)
     */
    inline fun <reified T : Any> getOrNull(): T? = koinContext.resolve<T>()

    /**
     * Retrieve a property
     */
    @Throws(MissingPropertyException::class)
    inline fun <reified T> getProperty(key: String): T = koinContext.propertyResolver.getProperty(key)

    /**
     * Retrieve safely a property
     */
    inline fun <reified T> getPropertyOrNull(key: String): T? = koinContext.propertyResolver.getPropertyOrNull(key)

    /**
     * Set a property
     */
    fun setProperty(key: String, value: Any) = koinContext.propertyResolver.setProperty(key, value)
}