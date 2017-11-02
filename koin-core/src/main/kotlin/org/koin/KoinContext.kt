package org.koin

import org.koin.Koin.Companion.logger
import org.koin.core.bean.BeanDefinition
import org.koin.core.bean.BeanRegistry
import org.koin.core.instance.InstanceFactory
import org.koin.core.property.PropertyRegistry
import org.koin.core.scope.Scope
import org.koin.error.DependencyResolutionException
import java.util.*
import kotlin.reflect.KClass

/**
 * Koin Application Context
 * Context from where you can get beans defines in modules
 *
 * @author Arnaud GIULIANI
 */
class KoinContext(val beanRegistry: BeanRegistry, val propertyResolver: PropertyRegistry, val instanceFactory: InstanceFactory) {

    /**
     * call stack - bean definition resolution
     */
    val resolutionStack = Stack<KClass<*>>()

    /**
     * Retrieve a bean instance
     */
    inline fun <reified T> get(name: String = ""): T = if (name.isEmpty()) resolveByClass() else resolveByName(name)

    /**
     * Resolve a dependency for its bean definition
     * @param beandefinition name
     */
    inline fun <reified T> resolveByName(name: String) = resolveInstance<T> { beanRegistry.searchByName(name) }

    /**
     * Resolve a dependency for its bean definition
     * byt Its infered type
     */
    inline fun <reified T> resolveByClass(): T = resolveInstance { beanRegistry.searchAll(T::class) }

    /**
     * Resolve a dependency for its bean definition
     */
    inline fun <reified T> resolveInstance(resolver: () -> BeanDefinition<*>): T {
        val clazz = T::class

        logger.log("Resolving $clazz")

        if (resolutionStack.contains(clazz)) {
            logger.log("Cyclic dependency error stack : $resolutionStack")
            throw DependencyResolutionException("Cyclic dependency for $clazz")
        }
        resolutionStack.add(clazz)

        val beanDefinition: BeanDefinition<*> = resolver()

        val instance = instanceFactory.retrieveInstance<T>(beanDefinition)

        logger.log("Got instance  $instance")

        val head = resolutionStack.pop()
        if (head != clazz) {
            resolutionStack.clear()
            throw IllegalStateException("Calling HEAD was $head but must be $clazz")
        }
        return instance
    }

    /**
     * Check the all the loaded definitions - Try to resolve each definition
     */
    fun dryRun() {
        logger.log("(KOIN - DRY RUN)")
        beanRegistry.definitions.keys.forEach { def ->
            instanceFactory.retrieveInstance<Any>(def)
        }
    }

    /**
     * Drop all instances for given context
     * @param name
     */
    fun release(name: String) {

        logger.log("Release context : $name")

        val definitions: List<BeanDefinition<*>> = beanRegistry.getDefinitionsFromScope(name)
        instanceFactory.dropAllInstances(definitions)
    }

    /**
     * Retrieve a property by its key
     * can throw MissingPropertyException if the property is not found
     * @param key
     */
    inline fun <reified T> getProperty(key: String): T = propertyResolver.getProperty(key)

    /**
     * Retrieve a property by its key or return provided default value
     * @param key
     * @param value
     */
    inline fun <reified T> getPropertyOrElse(key: String, defaultValue: T): T = propertyResolver.getPropertyOrElse(key, defaultValue)

    /**
     * Set a property
     * @param key
     * @param value
     */
    fun setProperty(key: String, value: Any) = propertyResolver.setProperty(key, value)

    /**
     * Delete properties from keys
     * @param keys
     */
    fun removeProperties(vararg keys: String) {

        logger.log("Remove keys : $keys")

        keys.forEach { propertyResolver.delete(it) }
    }

    /**
     * Provide a bean definition
     * @param name - component name (default "")
     * @param bind - assignable class (default is null)
     * @param scopeName - scope name (default is null)
     * @param definition - component definition function
     */
    inline fun <reified T> provide(name: String = "", bind: KClass<*>? = null, scopeName: String? = null, noinline definition: () -> T) {
        val beanDefinition = BeanDefinition(name, T::class, definition = definition)
        bind?.let {
            beanDefinition.bind(bind)
        }
        val scope = if (scopeName != null) beanRegistry.getScope(scopeName) else beanRegistry.getScope(Scope.ROOT)
        beanRegistry.declare(beanDefinition, scope)
    }
}