package org.koin.instance

import org.koin.bean.BeanDefinition
import org.koin.dsl.context.Scope
import org.koin.error.BeanDefinitionException
import kotlin.reflect.KClass

/**
 * Manage all InstanceFactory per Scope
 */
class InstanceResolver {

    val all_context = HashMap<Scope, InstanceFactory>()

    fun getInstanceFactory(scope: Scope) = all_context[scope] ?: throw BeanDefinitionException("couldn't resolveInstance scope $scope")

    fun <T> resolveInstance(def: BeanDefinition<*>): T {
        val instanceFactory = getInstanceFactory(def.scope)
        return instanceFactory.resolveInstance(def, def.scope)
    }

    fun deleteInstance(vararg classes: KClass<*>, scope: Scope) {
        val instanceFactory = getInstanceFactory(scope)
        classes.forEach { instanceFactory.deleteInstance(it) }
    }

    fun createContext(scope: Scope) {
        if (!all_context.containsKey(scope)) {
            all_context[scope] = InstanceFactory()
        }
    }
}