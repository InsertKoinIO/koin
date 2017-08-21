package org.koin.bean

import org.koin.dsl.context.Scope
import kotlin.reflect.KClass

/**
 * Bean registry
 * gather definitions of beans & communicate with instance factory to handle instances
 * @author - Arnaud GIULIANI
 */
class BeanRegistry {

    val logger: java.util.logging.Logger = java.util.logging.Logger.getLogger(BeanRegistry::class.java.simpleName)

    /*
        bean definitions
     */
    val definitions = HashSet<BeanDefinition<*>>()

    /**
     * Add/Replace an existing bean
     *
     * @param def : Bean definition
     */
    fun declare(def: BeanDefinition<*>) {
        logger.info(">> Declare bean definition $def")

        val found = search(def.clazz)
        if (found != null) {
            remove(def.clazz)
        }
        definitions += def
    }

    /**
     * Add/Replace an existing bean
     *
     * @param function : Declaration function bean
     * @param clazz : Bean Type
     * @param scope : Bean scope
     */
    inline fun <reified T : Any> declare(noinline function: () -> T, clazz: kotlin.reflect.KClass<*> = T::class, scope: Scope) {
        val def = BeanDefinition(function, clazz, scope)

        logger.info(">> Declare bean definition $def")

        val found = search(clazz)
        if (found != null) {
            remove(clazz)
        }
        definitions += def
    }

    /**
     * Search for any bean definition
     */
    fun searchAll(clazz: kotlin.reflect.KClass<*>) = search(clazz) ?: searchCompatible(clazz)

    /**
     * Search for a bean definition
     */
    fun search(clazz: kotlin.reflect.KClass<*>): BeanDefinition<*>? = definitions.firstOrNull { it.clazz == clazz }

    /**
     * Search for a compatible bean definition (subtype type of given clazz)
     */
    private fun searchCompatible(clazz: kotlin.reflect.KClass<*>): BeanDefinition<*>? = definitions.firstOrNull { it.bindTypes.contains(clazz) }

    /**
     * move any definition for given class
     * @param classes Class
     */
    fun remove(vararg classes: KClass<*>) {
        logger.warning("removeInstance $classes")
        classes.map { search(it) }.forEach { definitions.remove(it) }
    }
}