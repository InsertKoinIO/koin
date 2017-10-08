package org.koin.core.instance

import org.koin.core.scope.Scope
import kotlin.reflect.KClass

/**
 * Manage all InstanceFactory per Scope
 */
class InstanceResolver {

    val scopes = arrayListOf<Scope>()
    val rootScope = Scope.root()

    init {
        scopes += rootScope
    }

//    fun getInstanceFactory(scope: Scope) = all_context[scope] ?: throw BeanDefinitionException("couldn't resolveInstance scope $scope")
//
//    fun <T> resolveInstance(def: BeanDefinition<*>): T {
//        val instanceFactory = getInstanceFactory(def.scope)
//        return instanceFactory.resolveInstance(def, def.scope)
//    }
//
//    fun deleteInstance(vararg classes: KClass<*>, scope: Scope) {
//        val instanceFactory = getInstanceFactory(scope)
//        classes.forEach { instanceFactory.deleteInstance(it) }
//    }
//
//    fun createContext(scope: Scope) {
//        if (!all_context.containsKey(scope)) {
//            all_context[scope] = InstanceFactory()
//        }
//    }

//    fun scopeExists(scope : Scope) = all_context[scope] != null

    fun findOrCreateScope(scope: KClass<*>?, parentScope: KClass<*>? = null): Scope {
        return if (scope == null) rootScope
        else {
            scopes.firstOrNull { it.clazz == scope } ?: createScope(scope, parentScope)
        }
    }

    fun createScope(scope: KClass<*>, parentScope: KClass<*>?): Scope {
        val s = Scope(scope, parentScope = findOrCreateScope(parentScope))
        scopes += s
        return s
    }
}