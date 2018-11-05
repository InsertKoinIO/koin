package org.koin.core.instance

import org.koin.core.KoinApplication
import org.koin.core.bean.BeanDefinition
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.scope.getScopeId

class ScopeInstance<T>(private val beanDefinition: BeanDefinition<T>) : Instance<T> {

    val scopeId =
        beanDefinition.getScopeId() ?: error("Try to create a ScopeInstance with non scoped definition $beanDefinition")

    override fun isAlreadyCreated(): Boolean = (value != null)

    var value: T? = null

    override fun release() {
        KoinApplication.log("[Koin] releasing '$scopeId' ~ $beanDefinition ")
        value = null
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(parameters: ParametersDefinition?): T {
        if (value == null) {
            value = create(beanDefinition, parameters)
        }
        return value as? T ?: error("Scope instance created couldn't return value")
    }
}