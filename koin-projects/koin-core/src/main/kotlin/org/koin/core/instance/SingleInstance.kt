package org.koin.core.instance

import org.koin.core.bean.BeanDefinition
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.scope.Scope

class SingleInstance<T>(beanDefinition: BeanDefinition<T>) : Instance<T>(beanDefinition) {

    override fun isCreated(scope: Scope?): Boolean = (value != null)

    private var value: T? = null

    override fun release(scope: Scope?) {}

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(scope: Scope?, parameters: ParametersDefinition?): T {
        if (value == null) {
            value = create(beanDefinition, parameters)
        }
        return value as? T ?: error("Single instance created couldn't return value")
    }
}