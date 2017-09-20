package org.koin.instance

import org.koin.bean.BeanDefinition
import org.koin.dsl.context.Scope
import org.koin.error.BeanDefinitionException
import org.koin.error.BeanInstanceCreationException
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Instance factory - handle objects creation for BeanRegistry
 * @author - Arnaud GIULIANI
 */
@Suppress("UNCHECKED_CAST")
class InstanceFactory {

//    private val logger: Logger = Logger.getLogger(InstanceFactory::class.java.simpleName)

    val instances = ConcurrentHashMap<BeanDefinition<*>, Any>()

    /**
     * Retrieve or create bean instance
     */
    private fun <T> retrieveInstance(def: BeanDefinition<*>, scope: Scope): T {
        var instance = findInstance<T>(def)
        if (instance == null) {
            instance = createInstance(def, scope)
        }
        return instance!!
    }

    /**
     * Find existing instance
     */
    private fun <T> findInstance(def: BeanDefinition<*>): T? {
        val existingClass = instances.keys.firstOrNull { it == def }
        return if (existingClass != null) {
            instances[existingClass] as? T
        } else {
            null
        }
    }

    /**
     * create instance for given bean definition
     */
    private fun <T> createInstance(def: BeanDefinition<*>, scope: Scope): T {
//        logger.fine(">> Create instance : $def")
        return if (def.scope == scope) {
            try {
                val instance = def.definition.invoke() as Any
                instances[def] = instance
                instance as T
            } catch (e: Throwable) {
                throw BeanInstanceCreationException("Can't create bean $def due to error : $e")
            }
        } else throw BeanDefinitionException("Can't create bean $def in scope : $scope -- Scope has not been declared")
    }

    fun <T> resolveInstance(def: BeanDefinition<*>, scope: Scope) = retrieveInstance<T>(def, scope)

    fun deleteInstance(vararg kClasses: KClass<*>) {
        kClasses.forEach { clazz ->
            val res = instances.keys.filter { it.clazz == clazz }
            res.forEach { def -> instances.remove(def) }
        }
    }

    fun clear() {
        instances.clear()
    }
}