package org.koin.bean

import org.koin.context.Scope
import org.koin.error.BeanDefinitionException
import org.koin.error.NoBeanDefFoundException
import org.koin.instance.InstanceFactory
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubclassOf

/**
 * Bean registry
 * gather definitions of beans & communicate with instance factory to handle instances
 * @author - Arnaud GIULIANI
 */
class BeanRegistry() {

    val logger: java.util.logging.Logger = java.util.logging.Logger.getLogger(BeanRegistry::class.java.simpleName)

    val definitions = HashSet<BeanDefinition<*>>()

    /**
     * Add/Replace an existing bean

     * @param o
     * *
     * @param clazz
     */
    inline fun <reified T : Any> declare(noinline function: () -> T, clazz: kotlin.reflect.KClass<*> = T::class, scope: Scope) {
        val def = BeanDefinition(function, clazz, scope)

        logger.info("declare bean definition $def")

        val found = search(clazz)
        if (found != null) {
            remove(clazz)
        }
        definitions += def
    }

//    /**
//     * Declare a bean with its Ctor
//     */
//    fun declareFromConstructor(clazz: KClass<*>, instanceFactory: InstanceFactory, scope: Scope) {
//        if (clazz.constructors.isEmpty()) {
//            throw BeanDefinitionException("class $clazz has no constructor")
//        } else {
//            val ctor = clazz.constructors.first()
//            val types = ctor.parameters
//            if (types.isEmpty()) {
//                declare({ clazz.createInstance() }, clazz, scope)
//            } else {
//                declare({
//                    val instances: Map<KParameter, Any> = types.map { searchAll(it.type.classifier as KClass<*>) }.map { it to instanceFactory.resolveInstance(it) }.toMap()
//                    ctor.callBy(instances)
//                }, clazz, scope)
//            }
//        }
//    }

    /**
     * Search for any bean definition
     */
    fun searchAll(clazz: kotlin.reflect.KClass<*>) = search(clazz) ?: searchCompatible(clazz) ?: throw NoBeanDefFoundException("No bean definition found for $clazz")

    /**
     * Search for a bean definition
     */
    fun search(clazz: kotlin.reflect.KClass<*>): BeanDefinition<*>? = definitions.filter { it.clazz == clazz }.firstOrNull()

    /**
     * Search for a compatible bean definition (subtype type of given clazz)
     */
    private fun searchCompatible(clazz: kotlin.reflect.KClass<*>): BeanDefinition<*>? = definitions.filter { it.clazz.isSubclassOf(clazz) }.firstOrNull()

    /**
     * removeInstance a bean and its instance
     * @param clazz Class
     */
    fun remove(vararg classes: kotlin.reflect.KClass<*>) {
        logger.warning("removeInstance $classes")

        classes.map { search(it) }.forEach { definitions.remove(it) }
    }
}