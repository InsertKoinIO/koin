package org.koin

import org.koin.bean.BeanDefinition
import org.koin.bean.BeanType
import org.koin.error.NoBeanDefFoundException
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.isSubclassOf

/**
 * Bean registry
 * gather definitions of beans & communicate with instance factory to handle instances
 * @author - Arnaud GIULIANI
 */
class BeanRegistry(val instanceFactory: InstanceFactory = InstanceFactory()) {

    val logger: java.util.logging.Logger = java.util.logging.Logger.getLogger(org.koin.BeanRegistry::class.java.simpleName)

    val definitions = HashSet<BeanDefinition<*>>()

    /**
     * Add/Replace an existing bean

     * @param o
     * *
     * @param clazz
     */
    inline fun <reified T : Any> declare(noinline function: () -> T, clazz: kotlin.reflect.KClass<*> = T::class, type: BeanType = BeanType.SINGLETON) {
        val def = BeanDefinition(function, clazz, type)

        logger.info("declare bean definition $def")

        val found = searchDefinition(clazz)
        // overwrite existing definition
        if (found != null) {
            remove(clazz)
        }
        definitions += def
    }

    /**
     * Search for a bean definition
     */
    fun searchDefinition(clazz: kotlin.reflect.KClass<*>): BeanDefinition<*>? = definitions.filter { it.clazz == clazz }.firstOrNull()

    /**
     * Search for a compatible bean definition (subtype type of given clazz)
     */
    fun searchCompatibleType(clazz: kotlin.reflect.KClass<*>): BeanDefinition<*>? = definitions.filter { it.clazz.isSubclassOf(clazz) }.firstOrNull()


    /**
     * Resolve property injection for given target instance
     */
    fun <T : Any> resolveInjection(target: Any, member: KMutableProperty<T>) {
        val instance: Any = resolveInstance(member.returnType.classifier as KClass<*>)
        member.setter.call(target, instance)
    }

    /**
     * Retrieve a bean instance for given Clazz
     * @param clazz
     */
    @Throws(NoBeanDefFoundException::class)
    fun <T> resolveInstance(clazz: kotlin.reflect.KClass<*>): T {
        logger.info("resolve instance for $clazz")

        var def = searchDefinition(clazz)
        if (def == null) {
            def = searchCompatibleType(clazz)
            logger.info("found compatible type for $clazz ? $def")
        }

        if (def != null) {
            return when (def.type) {
                BeanType.FACTORY -> instanceFactory.createInstance(def, clazz, false)
                BeanType.SINGLETON -> {
                    instanceFactory.retrieveOrCreateInstance<T>(clazz, def)
                }
                BeanType.STACK -> {
                    val instance = instanceFactory.retrieveOrCreateInstance<T>(clazz, def, false)
                    remove(clazz)
                    instance
                }
            }
        } else {
            throw NoBeanDefFoundException("Can't find bean definition for $clazz")
        }
    }

    /**
     * remove a bean and its instance
     * @param clazz Class
     */
    fun remove(clazz: kotlin.reflect.KClass<*>) {
        logger.warning("remove $clazz")

        val found = searchDefinition(clazz)
        if (found != null) {
            definitions.remove(found)
            instanceFactory.instances.remove(clazz)
        }
    }
}