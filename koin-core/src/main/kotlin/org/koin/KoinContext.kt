package org.koin

import org.koin.bean.BeanRegistry
import org.koin.dsl.context.Scope
import org.koin.error.CyclicDependencyException
import org.koin.error.InstanceNotFoundException
import org.koin.error.MissingPropertyException
import org.koin.instance.InstanceResolver
import org.koin.property.PropertyResolver
import java.util.*
import java.util.logging.Logger
import kotlin.reflect.KClass

/**
 * Koin Application Context
 * Context from where you can get beans defines in modules
 *
 * @author Arnaud GIULIANI
 */
class KoinContext(val beanRegistry: BeanRegistry, val propertyResolver: PropertyResolver, val instanceResolver: InstanceResolver) {

    val logger: Logger = Logger.getLogger(KoinContext::class.java.simpleName)

    /**
     * Retrieve a bean instance
     */
    inline fun <reified T> get(name: String = ""): T = getOrNull<T>(name) ?: throw InstanceNotFoundException("No instance found for ${T::class}")

    /**
     * Safely Retrieve a bean instance (can be null)
     */
    inline fun <reified T> getOrNull(name: String = ""): T? = if (name.isEmpty()) resolve<T>() else resolveByName<T>(name)

    /**
     * resolution stack
     */
    val resolutionStack = Stack<KClass<*>>()

    /**
     * Resolve a dependency for its bean definition
     */
    inline fun <reified T> resolveByName(name: String): T? {
        val clazz = T::class
        logger.info("resolve $clazz :: $resolutionStack")

        if (resolutionStack.contains(clazz)) {
            throw CyclicDependencyException("Cyclic dependency for $clazz")
        }
        resolutionStack.add(clazz)

        val instance = instanceResolver.resolveInstance<T>(beanRegistry.searchByName(name))
        val head = resolutionStack.pop()
        if (head != clazz) {
            throw IllegalStateException("Calling HEAD was $head but must be $clazz")
        }
        return instance
    }

    /**
     * Resolve a dependency for its bean definition
     */
    inline fun <reified T> resolve(): T? {
        val clazz = T::class
        logger.info("resolve $clazz :: $resolutionStack")

        if (resolutionStack.contains(clazz)) {
            throw CyclicDependencyException("Cyclic dependency for $clazz")
        }
        resolutionStack.add(clazz)

        val instance = instanceResolver.resolveInstance<T>(beanRegistry.searchAll(clazz))
        val head = resolutionStack.pop()
        if (head != clazz) {
            throw IllegalStateException("Calling HEAD was $head but must be $clazz")
        }
        return instance
    }

    //TODO provide with name

    /**
     * provide bean definition at Root scope
     * @param definition function declaration
     */
    inline fun <reified T : Any> provide(noinline definition: () -> T) {
        logger.finest("declare singleton $definition")
        declare(definition, Scope.root())
    }

    /**
     * provide bean definition at given class/scope
     * @param definition  function declaration
     */
    inline fun <reified T : Any> provideAt(noinline definition: () -> T, scopeClass: KClass<*>) {
        val scope = Scope(scopeClass)
        val existingScope = instanceResolver.all_context[scope]
        if (existingScope == null) {
            instanceResolver.createContext(scope)
        }
        declare(definition, scope)
    }

    /**
     * provide bean definition at given scope
     * @param definition  function declaration
     */
    inline fun <reified T : Any> declare(noinline definition: () -> T, scope: Scope) {
        instanceResolver.deleteInstance(T::class, scope = scope)
        beanRegistry.declare(definition, T::class, scope = scope)
    }

    /**
     * Clear given scope instance
     */
    fun release(vararg scopeClasses: KClass<*>) {
        scopeClasses.forEach {
            logger.warning("Clear instance $it ")
            instanceResolver.getInstanceFactory(Scope(it)).clear()
        }
    }

    /**
     * Clear given Root instance
     */
    fun release() {
        logger.warning("Clear instance ROOT")
        instanceResolver.getInstanceFactory(Scope.root()).clear()
    }

    /**
     * Retrieve a property
     */
    @Throws(MissingPropertyException::class)
    inline fun <reified T> getProperty(key: String): T = getPropertyOrNull(key) ?: throw MissingPropertyException("Could not bind property $key")

    /**
     * Retrieve safely a property
     */
    inline fun <reified T> getPropertyOrNull(key: String): T? = propertyResolver.getProperty(key)

    /**
     * Set a property
     */
    fun setProperty(key: String, value: Any) = propertyResolver.setProperty(key, value)
}