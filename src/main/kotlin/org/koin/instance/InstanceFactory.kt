package org.koin.instance

import org.koin.bean.BeanDefinition
import org.koin.dsl.context.Scope
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger
import kotlin.reflect.KClass

/**
 * Instance factory - handle objects instances from BeanRegistry
 * @author - Arnaud GIULIANI
 */
@Suppress("UNCHECKED_CAST")
class InstanceFactory {

    val logger: Logger = Logger.getLogger(InstanceFactory::class.java.simpleName)

    val instances = ConcurrentHashMap<KClass<*>, Any>()

    /**
     * Retrieve or create bean instance
     */
    fun <T> retrieveInstance(def: BeanDefinition<*>, clazz: KClass<*>, scope: Scope): T? {
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
        val existingClass = instances.keys.filter { it == clazz }.firstOrNull()
        if (existingClass != null) {
            return instances[existingClass] as? T
        } else {
            return null
        }
    }

    /**
     * create instance for given bean definition
     */
    private fun <T> createInstance(def: BeanDefinition<*>, clazz: KClass<*>, scope: Scope): T? {
        logger.fine(">> Create instance : $def")
        if (def.scope == scope) {
            val instance = def.definition.invoke() as Any
            instances[clazz] = instance
            return instance as T
        } else return null
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