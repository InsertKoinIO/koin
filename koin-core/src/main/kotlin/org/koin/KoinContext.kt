package org.koin

import org.koin.core.bean.BeanRegistry
import org.koin.core.instance.InstanceResolver
import org.koin.core.property.PropertyResolver
import java.util.*
import kotlin.reflect.KClass

/**
 * Koin Application Context
 * Context from where you can get beans defines in modules
 *
 * @author Arnaud GIULIANI
 */
class KoinContext(val beanRegistry: BeanRegistry, val propertyResolver: PropertyResolver, val instanceResolver: InstanceResolver) {

    /**
     * resolution stack
     */
    val resolutionStack = Stack<KClass<*>>()

    /**
     * Retrieve a bean instance
     */
    inline fun <reified T> get(name: String = ""): T = null as T //if (name.isEmpty()) resolveByClass() else resolveByName(name)

//    /**
//     * Resolve a dependency for its bean definition
//     */
//    inline fun <reified T> resolveByName(name: String) = resolveInstance<T> { beanRegistry.searchByName(name) }
//
//    /**
//     * Resolve a dependency for its bean definition
//     */
//    inline fun <reified T> resolveByClass(): T = resolveInstance { beanRegistry.searchAll(T::class) }
//
//    /**
//     * Resolve a dependency for its bean definition
//     */
//    inline fun <reified T> resolveInstance(resolver: () -> BeanDefinition<*>): T {
//        val clazz = T::class
//        if (resolutionStack.contains(clazz)) {
//            throw DependencyResolutionException("Cyclic dependency for $clazz")
//        }
//        resolutionStack.add(clazz)
//
//        val beanDefinition: BeanDefinition<*> = resolver()
//
//        val instance = instanceResolver.resolveInstance<T>(beanDefinition)
//        val head = resolutionStack.pop()
//        if (head != clazz) {
//            throw IllegalStateException("Calling HEAD was $head but must be $clazz")
//        }
//        return instance
//    }


//    /**
//     * provide bean definition at Root scope
//     * @param definition function declaration
//     */
//    inline fun <reified T : Any> provide(noinline definition: () -> T) {
//        declare(definition, Scope.root())
//    }
//
//    /**
//     * provide bean definition at given class/scope
//     * @param definition  function declaration
//     */
//    inline fun <reified T : Any> provideAt(noinline definition: () -> T, scopeClass: KClass<*>) {
//        val scope = Scope(scopeClass)
//        instanceResolver.all_context[scope] ?: throw BeanDefinitionException("No scope defined for class $scopeClass")
//        declare(definition, scope)
//    }
//
//    /**
//     * Create a scope
//     */
//    fun declareScope(scopedClass: KClass<*>) {
//        instanceResolver.createContext(Scope(scopedClass))
//    }
//
//    /**
//     * provide bean definition at given scope
//     * @param definition  function declaration
//     */
//    inline fun <reified T : Any> declare(noinline definition: () -> T, scope: Scope) {
//        instanceResolver.deleteInstance(T::class, scope = scope)
//        beanRegistry.declare(definition, T::class, scope = scope)
//    }
//
//    /**
//     * Clear given class/scope instance
//     */
//    private fun release(scopedClass: KClass<*>) {
////        logger.warning("Clear instance $scopedClass ")
//        instanceResolver.getInstanceFactory(Scope(scopedClass)).clear()
//    }
//
//    /**
//     * Clear given class/scope instance from objects
//     */
//    fun release(vararg scopedObject: Any) = scopedObject.map { it::class }.forEach { release(it) }
//
//    /**
//     * Retrieve a property by its key
//     * can return null
//     */
//    inline fun <reified T> getProperty(key: String): T? = propertyResolver.getProperty(key)
//
//    /**
//     * Set a property
//     */
//    fun setProperty(key: String, value: Any?) = propertyResolver.setProperty(key, value)
}