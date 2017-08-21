package org.koin.instance

import org.koin.bean.BeanDefinition
import org.koin.dsl.context.Scope
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger
import kotlin.reflect.KClass

/**
 * Instance factory - handle objects creation for BeanRegistry
 * @author - Arnaud GIULIANI
 */
@Suppress("UNCHECKED_CAST")
class InstanceFactory {

    private val logger: Logger = Logger.getLogger(InstanceFactory::class.java.simpleName)

    val instances = ConcurrentHashMap<KClass<*>, Any>()

    /**
     * Retrieve or create bean instance
     */
    private fun <T> retrieveInstance(def: BeanDefinition<*>, clazz: KClass<*>, scope: Scope): T? {
        var instance = findInstance<T>(clazz)
        if (instance == null) {
            instance = createInstance(def, clazz, scope)
        }
        return instance
    }

    /**
     * Find existing instance
     */
    private fun <T> findInstance(clazz: KClass<*>): T? {
        val existingClass = instances.keys.firstOrNull { it == clazz }
        return if (existingClass != null) {
            instances[existingClass] as? T
        } else {
            null
        }
    }

    /**
     * create instance for given bean definition
     */
    private fun <T> createInstance(def: BeanDefinition<*>, clazz: KClass<*>, scope: Scope): T? {
        logger.fine(">> Create instance : $def")
        return if (def.scope == scope) {
            try {
                val instance = def.definition.invoke() as Any
                instances[clazz] = instance
                instance as T
            } catch(e: Exception) {
                logger.warning("Couldn't get instance for $def due to error $e")
                null
            }
        } else null
    }

    fun <T> resolveInstance(def: BeanDefinition<*>, scope: Scope): T? {
        return retrieveInstance<T>(def, def.clazz, scope)
    }

    fun deleteInstance(vararg kClasses: KClass<*>) {
        kClasses.forEach { instances.remove(it) }
    }

    fun clear() {
        instances.clear()
    }
}