package org.koin.instance

import org.koin.bean.BeanDefinition
import org.koin.dsl.context.Scope
import java.util.logging.Logger
import kotlin.reflect.KClass

/**
 * Manage all InstanceFactory per Scope
 */
class InstanceResolver() {

    val logger: Logger = Logger.getLogger(InstanceResolver::class.java.simpleName)

    val all_context = HashMap<Scope, InstanceFactory>()

    fun getInstanceFactory(scope: Scope) = all_context[scope] ?: throw IllegalStateException("couldn't resolve scope $scope")

    fun <T> resolveInstance(def: BeanDefinition<*>?): T? {
        if (def == null) return null
        else {
            val instanceFactory = getInstanceFactory(def.scope)
            return instanceFactory.resolveInstance<T>(def, def.scope)
        }
    }

    fun deleteInstance(vararg classes: KClass<*>, scope: Scope) {
        val instanceFactory = getInstanceFactory(scope)
        classes.forEach { instanceFactory.deleteInstance(it) }
    }

    fun createContext(scope: Scope) {
        if (!all_context.containsKey(scope)) {
            all_context[scope] = InstanceFactory()
            logger.info(">> Create scope $scope")
        }
    }
}