package org.koin.instance

import org.koin.bean.BeanDefinition
import org.koin.context.Scope
import org.koin.error.ScopeNotFoundException
import java.util.logging.Logger
import kotlin.reflect.KClass

/**
 * Created by arnaud on 28/06/2017.
 */
class InstanceResolver() {

    val logger: Logger = Logger.getLogger(InstanceResolver::class.java.simpleName)

    val all_context = HashMap<Scope, InstanceFactory>()

    fun getInstanceFactory(scope: Scope) = all_context[scope]

    fun <T> resolveInstance(def: BeanDefinition<*>, scope: Scope = Scope.root()): T {
        val instanceFactory = getInstanceFactory(scope) ?: throw ScopeNotFoundException("couldn't resolve scope $scope")
        logger.info("--> resolve instance in scope : $instanceFactory")
        return instanceFactory.resolveInstance<T>(def)
    }

    fun deleteInstance(kClass: KClass<*>, scope: Scope = Scope.root()) {
        val instanceFactory = getInstanceFactory(scope) ?: throw ScopeNotFoundException("couldn't resolve scope $scope")
        return instanceFactory.deleteInstance(kClass)
    }

    fun createContext(scope: Scope) {
        if (!all_context.containsKey(scope)) {
            all_context[scope] = InstanceFactory()
            logger.info("--> create scope $scope")
        }
    }
}