//package org.koin.core.instance
//
//import org.koin.core.bean.BeanDefinition
//import java.util.concurrent.ConcurrentHashMap
//
///**
// * Instance factory - handle objects creation for BeanRegistry
// * @author - Arnaud GIULIANI
// */
//class InstanceFactory {
//
//    val instances = ConcurrentHashMap<BeanDefinition<*>, Any>()
//
////    /**
////     * Retrieve or create bean instance
////     */
////    private fun <T> retrieveInstance(def: BeanDefinition<*>, getScope: Scope): T {
////        var instance = findInstance<T>(def)
////        if (instance == null) {
////            instance = createInstance(def, getScope)
////        }
////        return instance!!
////    }
////
////    /**
////     * Find existing instance
////     */
////    private fun <T> findInstance(def: BeanDefinition<*>): T? {
////        val existingClass = instances.keys.firstOrNull { it == def }
////        return if (existingClass != null) {
////            instances[existingClass] as? T
////        } else {
////            null
////        }
////    }
////
////    /**
////     * create instance for given bean definition
////     */
////    private fun <T> createInstance(def: BeanDefinition<*>, getScope: Scope): T {
////        return if (def.getScope == getScope) {
////            try {
////                val instance = def.definition.invoke() as Any
////                instances[def] = instance
////                instance as T
////            } catch (e: Throwable) {
////                throw BeanInstanceCreationException("Can't create bean $def due to error : $e")
////            }
////        } else throw BeanDefinitionException("Can't create bean $def in getScope : $getScope -- Scope has not been declared")
////    }
////
////    fun <T> resolveInstance(def: BeanDefinition<*>, getScope: Scope) = retrieveInstance<T>(def, getScope)
////
////    fun deleteInstance(vararg kClasses: KClass<*>) {
////        kClasses.forEach { clazz ->
////            val res = instances.keys.filter { it.clazz == clazz }
////            res.forEach { def -> instances.remove(def) }
////        }
////    }
////
////    fun clear() {
////        instances.clear()
////    }
//}