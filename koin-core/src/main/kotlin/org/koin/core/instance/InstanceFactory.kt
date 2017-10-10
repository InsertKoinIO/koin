package org.koin.core.instance

import org.koin.core.bean.BeanDefinition
import org.koin.core.bean.BeanRegistry
import org.koin.error.BeanDefinitionException
import org.koin.error.BeanInstanceCreationException
import java.util.concurrent.ConcurrentHashMap

/**
 * Instance factory - handle objects creation against BeanRegistry
 * @author - Arnaud GIULIANI
 */
class InstanceFactory(val beanRegistry: BeanRegistry) {

    val instances = ConcurrentHashMap<BeanDefinition<*>, Any>()

    /**
     * Retrieve or create bean instance
     */
    fun <T> retrieveInstance(def: BeanDefinition<*>): T {
        // Factory
        return if (def.isNotASingleton()) {
            createInstance(def)
        } else {
            // Singleton
            var instance = findInstance<T>(def)
            if (instance == null) {
                instance = createInstance(def)
                saveInstance(def, instance)
            }
            instance ?: throw BeanInstanceCreationException("Couldn't create instance for $def")
        }
    }

    private fun <T> saveInstance(def: BeanDefinition<*>, instance: T) {
        instances[def] = instance as Any
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
    private fun <T> createInstance(def: BeanDefinition<*>): T {
        val scope = beanRegistry.getScopeForDefinition(def)
        if (scope == null) throw BeanDefinitionException("Can't create bean $def in : $scope -- Scope has not been declared")
        else {
            try {
                val instance = def.definition.invoke() as Any
                instance as T
                return instance
            } catch (e: Throwable) {
                throw BeanInstanceCreationException("Can't create bean $def due to error : $e")
            }
        }
    }

    /**
     * TODO
     */
    fun dropAllInstances(definitions: List<BeanDefinition<*>>) {
        definitions.forEach { instances.remove(it) }
    }
}