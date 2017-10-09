//package org.koin.core.instance
//
//import org.koin.core.bean.BeanDefinition
//import java.util.concurrent.ConcurrentHashMap
//
///**
// * Manage all InstanceFactory per Scope
// */
//class InstanceResolver {
//
//    val instances = ConcurrentHashMap<BeanDefinition<*>, Any>()
//
//
////    fun getInstanceFactory(getScope: Scope) = all_context[getScope] ?: throw BeanDefinitionException("couldn't resolveInstance getScope $getScope")
////
////    fun <T> resolveInstance(def: BeanDefinition<*>): T {
////        val instanceFactory = getInstanceFactory(def.getScope)
////        return instanceFactory.resolveInstance(def, def.getScope)
////    }
////
////    fun deleteInstance(vararg classes: KClass<*>, getScope: Scope) {
////        val instanceFactory = getInstanceFactory(getScope)
////        classes.forEach { instanceFactory.deleteInstance(it) }
////    }
////
////    fun createContext(getScope: Scope) {
////        if (!all_context.containsKey(getScope)) {
////            all_context[getScope] = InstanceFactory()
////        }
////    }
////
////    fun scopeExists(getScope : Scope) = all_context[getScope] != null
//
//
//}