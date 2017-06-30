package org.koin.dsl.context

import org.koin.KoinContext
import org.koin.error.InstanceNotFoundException
import org.koin.error.MissingPropertyException
import kotlin.reflect.KClass

/**
 * Koin Context
 * Define dependencies & properties for actual context
 * @author - Arnaud GIULIANI
 */
class Context(val koinContext: KoinContext) {

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

    /**
     * Declarative DSL
     */

    var contextScope: Scope? = null

    inline fun <reified T : Any> provide(noinline definition: () -> T) {
        koinContext.beanRegistry.declare(definition, T::class, contextScope ?: Scope.root())
    }

    fun scope(definition: () -> KClass<*>) {
        contextScope = Scope(definition())
    }

    /**
     * Runtime resolutions
     */

    inline fun <reified T : Any> get(): T {
        return getOrNull<T>() ?: throw InstanceNotFoundException("no bean instance for ${T::class}")
    }

    inline fun <reified T : Any> getOrNull(): T? {
        return koinContext.instanceResolver.resolveInstance<T>(koinContext.beanRegistry.searchAll(T::class))
    }
}