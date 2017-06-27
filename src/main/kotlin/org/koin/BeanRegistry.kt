package org.koin

import org.koin.bean.BeanDefinition
import org.koin.bean.BeanType
import kotlin.reflect.full.isSubclassOf

/**
 * Bean registry
 * gather definitions of beans & communicate with instance factory to handle instances
 * @author - Arnaud GIULIANI
 */
class BeanRegistry() {

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
    fun searchCompatibleDefinition(clazz: kotlin.reflect.KClass<*>): BeanDefinition<*>? = definitions.filter { it.clazz.isSubclassOf(clazz) }.firstOrNull()

    /**
     * remove a bean and its instance
     * @param clazz Class
     */
    fun remove(vararg classes: kotlin.reflect.KClass<*>) {
        logger.warning("remove $classes")

        classes.map { searchDefinition(it) }.forEach { definitions.remove(it) }
    }
}