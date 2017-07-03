package org.koin

import org.koin.bean.BeanRegistry
import org.koin.dsl.context.Scope
import org.koin.error.InstanceNotFoundException
import org.koin.error.MissingPropertyException
import org.koin.instance.InstanceResolver
import org.koin.property.PropertyResolver
import java.util.logging.Logger
import kotlin.reflect.KClass

/**
 * Created by arnaud on 28/06/2017.
 */
class KoinContext(val beanRegistry: BeanRegistry, val propertyResolver: PropertyResolver, val instanceResolver: InstanceResolver) {

    val logger: Logger = Logger.getLogger(KoinContext::class.java.simpleName)

    /*
        Contextual to call !!!
     */
//    var currentScopes = arrayListOf<Scope>()

    /**
     * Retrieve a bean instance
     */
    inline fun <reified T> get(): T = getOrNull<T>() ?: throw InstanceNotFoundException("No instance found for ${T::class}")

    /**
     * Retrieve a bean instance or null
     */
    inline fun <reified T> getOrNull(): T? {
        return instanceResolver.resolveInstance<T>(beanRegistry.searchAll(T::class))
    }

    /**
     * provide bean definition
     * @param functional decleration
     */
    inline fun <reified T : Any> provide(noinline definition: () -> T) {
        logger.finest("declare singleton $definition")
        instanceResolver.deleteInstance(T::class, scope = Scope.root())
        beanRegistry.declare(definition, T::class, Scope.root())
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
     * Clear given scope instance
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