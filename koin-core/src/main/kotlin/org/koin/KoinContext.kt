package org.koin

import org.koin.bean.BeanDefinition
import org.koin.bean.BeanRegistry
import org.koin.dsl.context.Scope
import org.koin.error.CyclicDependencyException
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

//    val logger: Logger = Logger.getLogger(KoinContext::class.java.simpleName)

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
//        logger.info("resolveInstance $clazz :: $resolutionStack")

        if (resolutionStack.contains(clazz)) {
            throw CyclicDependencyException("Cyclic dependency for $clazz")
        }
        resolutionStack.add(clazz)

        val beanDefinition: BeanDefinition<*> = resolver()

        val instance = instanceResolver.resolveInstance<T>(beanDefinition)
        val head = resolutionStack.pop()
        if (head != clazz) {
            throw IllegalStateException("Calling HEAD was $head but must be $clazz")
        }
        return instance
    }


    /**
     * provide bean definition at Root scope
     * @param definition function declaration
     */
    inline fun <reified T : Any> provide(noinline definition: () -> T) {
//        logger.finest("declare singleton $definition")
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
     * Clear given class/scope instance
     */
    private fun release(scopedClass: KClass<*>) {
//        logger.warning("Clear instance $scopedClass ")
        instanceResolver.getInstanceFactory(Scope(scopedClass)).clear()
    }

    /**
     * Clear given class/scope instance from objects
     */
    fun release(vararg scopedObject: Any) = scopedObject.map { it::class }.forEach { release(it) }

    /**
     * Retrieve a property by its key
     * can return null
     */
    inline fun <reified T> getProperty(key: String): T? = propertyResolver.getProperty(key)

    /**
     * Set a property
     */
    fun setProperty(key: String, value: Any?) = propertyResolver.setProperty(key, value)
}