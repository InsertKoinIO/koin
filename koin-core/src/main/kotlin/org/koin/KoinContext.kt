package org.koin

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
     * resolution stack
     */
    val resolutionStack = Stack<KClass<*>>()

    /**
     * Retrieve a bean instance
     */
    inline fun <reified T> get(name: String = ""): T = if (name.isEmpty()) resolveByClass() else resolveByName(name)

    /**
     * Resolve a dependency for its bean definition
     */
    inline fun <reified T> resolveByName(name: String) = resolveInstance<T> { beanRegistry.searchByName(name) }

    /**
     * Resolve a dependency for its bean definition
     */
    inline fun <reified T> resolveByClass(): T = resolveInstance { beanRegistry.searchAll(T::class) }

    /**
     * Resolve a dependency for its bean definition
     */
    inline fun <reified T> resolveInstance(resolver: () -> BeanDefinition<*>): T {
        val clazz = T::class
        if (resolutionStack.contains(clazz)) {
            throw DependencyResolutionException("Cyclic dependency for $clazz")
        }
        resolutionStack.add(clazz)

        val beanDefinition: BeanDefinition<*> = resolver()

        val instance = instanceFactory.retrieveInstance<T>(beanDefinition)
        val head = resolutionStack.pop()
        if (head != clazz) {
            resolutionStack.clear()
            throw IllegalStateException("Calling HEAD was $head but must be $clazz")
        }
        return instance
    }

    /**
     * Try to inject each definition
     */
    fun dryRun() {
        beanRegistry.definitions.keys.forEach { def ->
            instanceFactory.retrieveInstance<Any>(def)
        }
    }

    /**
     * Provide a bean definition, on the fly
     */
    inline fun <reified T : Any> provide(contextName: String = Scope.ROOT, additionalBinding : KClass<*>? = null, noinline definition: () -> T) {
        val clazz = T::class
        instanceFactory.dropInstance(clazz)
        val beanDefinition = BeanDefinition(clazz = clazz, definition = definition)
        if (additionalBinding != null){
            beanDefinition.bind(additionalBinding)
        }
        beanRegistry.declare(beanDefinition, beanRegistry.getScope(contextName))
    }

    /**
     * Drop all instances for context
     * @param name
     */
    fun release(name: String) {
        val definitions: List<BeanDefinition<*>> = beanRegistry.getDefinitionsFromScope(name)
        instanceFactory.dropAllInstances(definitions)
    }

    /**
     * Retrieve a property by its key
     * can return null
     * @param key
     */
    inline fun <reified T> getProperty(key: String): T = propertyResolver.getProperty(key)

    /**
     * Set a property
     * @param key
     * @param value
     */
    fun setProperty(key: String, value: Any?) = propertyResolver.setProperty(key, value)
}