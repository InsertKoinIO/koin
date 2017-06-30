package org.koin.instance

import org.koin.bean.BeanDefinition
import org.koin.dsl.context.Scope
import org.koin.error.ScopeNotFoundException
import java.util.logging.Logger
import kotlin.reflect.KClass

/**
 * Created by arnaud on 28/06/2017.
 */
class InstanceResolver() {

    val logger: Logger = Logger.getLogger(InstanceResolver::class.java.simpleName)

    val all_context = HashMap<Scope, InstanceFactory>()

    fun getInstanceFactory(scope: Scope) = all_context[scope] ?: throw ScopeNotFoundException("couldn't resolve scope $scope")

    fun <T> resolveInstance(def: BeanDefinition<*>?, openedScopes: List<Scope>): T? {
        if (def == null) return null
        else {
            for (scope in openedScopes) {
                val instanceFactory = getInstanceFactory(scope)
                logger.info("Resolve ${def.clazz} @ scope $scope")
                val instance = instanceFactory.resolveInstance<T>(def, scope)
                if (instance != null) return instance
            }
            return null
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