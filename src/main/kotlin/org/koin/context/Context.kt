package org.koin.context

import org.koin.bean.BeanRegistry
import org.koin.error.MissingPropertyException
import org.koin.instance.InstanceResolver
import org.koin.module.Module
import org.koin.property.PropertyResolver
import java.util.logging.Logger

/**
 * Koin Context
 * Define dependencies & properties for actual context
 * @author - Arnaud GIULIANI
 */
class Context(val beanRegistry: BeanRegistry, val propertyResolver: PropertyResolver, val instanceResolver: InstanceResolver, val module: Module) {

    val logger: Logger = Logger.getLogger(Context::class.java.simpleName)

    /**
     * Retrieve a property
     */
    @Throws(MissingPropertyException::class)
    inline fun <reified T> getProperty(key: String): T = propertyResolver.getProperty(key)

    /**
     * Retrieve safely a property
     */
    inline fun <reified T> getPropertyOrNull(key: String): T? = propertyResolver.getPropertyOrNull(key)

    /**
     * Set a property
     */
    fun setProperty(key: String, value: Any) = propertyResolver.setProperty(key, value)

    /**
     * Retrieve a bean instance
     */
    inline fun <reified T> get(): T {
        return instanceResolver.resolveInstance(beanRegistry.searchAll(T::class), module.scope)
    }

    /**
     * Retrieve a bean instance or null
     */
    inline fun <reified T> getOrNull(): T? {
        try {
            return get<T>()
        } catch(e: Exception) {
            logger.warning("No bean found due to error $e - return null")
            return null
        }
    }

//    /**
//     * provide bean definition - As factory
//     * @param functional decleration
//     */
//    inline fun <reified T : Any> factory(noinline definition: () -> T) {
//        logger.finest("declare factory $definition")
//        beanRegistry.declare(definition, T::class, BeanType.FACTORY)
//    }

//    /**
//     * provide bean definition - As factory
//     * @param clazz
//     */
//    fun factory(clazz: KClass<*>) {
//        logger.finest("declare factory class $clazz")
//        provide(clazz, BeanType.FACTORY)
//    }

    /**
     * provide bean definition
     * @param functional decleration
     */
    inline fun <reified T : Any> provide(noinline definition: () -> T) {
        logger.finest("declare singleton $definition")
        instanceResolver.deleteInstance(T::class, module.scope)
        beanRegistry.declare(definition, T::class, module.scope)
    }

//    /**
//     * provide bean definition for class
//     * @param clazz
//     */
//    @Throws(BeanDefinitionException::class)
//    fun provide(clazz: KClass<*>) {
//        logger.finest("declare class : $clazz")
//        beanRegistry.declareFromConstructor(clazz, instanceResolver.getInstanceFactory(module.scope) ?: throw ScopeNotFoundException(""),module.scope)
//    }
}