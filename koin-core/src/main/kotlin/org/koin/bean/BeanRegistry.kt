package org.koin.bean

import org.koin.dsl.context.Scope
import org.koin.error.BeanDefinitionException
import org.koin.error.NoBeanDefFoundException
import kotlin.reflect.KClass

/**
 * Bean registry
 * gather definitions of beans & communicate with instance factory to handle instances
 * @author - Arnaud GIULIANI
 */
class BeanRegistry {

//    val logger: java.util.logging.Logger = java.util.logging.Logger.getLogger(BeanRegistry::class.java.simpleName)

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
//        logger.info(">> Declare bean definition $def")
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

//        logger.info(">> Declare bean definition $def")

        val found = searchByClass(clazz)
        if (found != null) {
            remove(clazz)
        }
        definitions += def
    }

    /**
     * Search bean by its name
     */
    fun searchByName(name: String) = searchDefinition({ it.name == name }, " name : $name") ?: throw NoBeanDefFoundException("No bean definition found for name $name")

    /**
     * Search for any bean definition
     */
    fun searchAll(clazz: kotlin.reflect.KClass<*>) = (searchByClass(clazz) ?: searchCompatible(clazz)) ?: throw NoBeanDefFoundException("No bean definition found for class $clazz")

    /**
     * Search for a bean definition
     */
    fun searchByClass(clazz: kotlin.reflect.KClass<*>) = searchDefinition({ it.clazz == clazz }, " class : $clazz")

    /**
     * Search definition with given filter function
     */
    private fun searchDefinition(filter: (BeanDefinition<*>) -> Boolean, errorMsg: String): BeanDefinition<*>? {
        val results = definitions.filter(filter)
        return if (results.size <= 1) results.firstOrNull()
        else throw BeanDefinitionException("Bean definition resolution error : no bean or multiple definition for $errorMsg")
    }

    /**
     * Search for a compatible bean definition (subtype type of given clazz)
     */
    private fun searchCompatible(clazz: kotlin.reflect.KClass<*>): BeanDefinition<*>? = searchDefinition({ it.bindTypes.contains(clazz) }, "for compatible type : $clazz")

    /**
     * move any definition for given class
     * @param classes Class
     */
    fun remove(vararg classes: KClass<*>) {
//        logger.warning("removeInstance $classes")
        classes.map { searchByClass(it) }.forEach { definitions.remove(it) }
    }

}