package org.koin.test.check

import org.koin.core.KoinApplication.Companion.logger
import org.koin.core.bean.BeanDefinition
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.error.InstanceCreationException
import org.koin.core.error.NoBeanDefFoundException
import org.koin.core.instance.Instance
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.scope.Scope
import org.koin.test.error.BrokenDefinitionException
import org.mockito.Mockito

@Suppress("UNCHECKED_CAST")
class SandboxInstance<T>(beanDefinition: BeanDefinition<T>) : Instance<T>(beanDefinition) {

    var value: T? = null

    override fun <T> get(scope: Scope?, parameters: ParametersDefinition?): T {
        if (value == null) {
            value = create(beanDefinition, parameters)
        }
        return value as? T ?: error("")
    }

    override fun <T> create(beanDefinition: BeanDefinition<*>, parameters: ParametersDefinition?): T {
        try {
            beanDefinition.instance.get<T>(null, parameters)
        } catch (e: Exception) {
            when (e) {
                is NoBeanDefFoundException, is InstanceCreationException, is DefinitionOverrideException -> {
                    throw BrokenDefinitionException("Definition $beanDefinition is broken due to error : $e")
                }
                else -> logger.debug("[Sandbox] continue on intercepted error: $e")
            }
        }
        val clazz = beanDefinition.primaryType.java
        return Mockito.mock(clazz) as T
    }

    override fun isCreated(scope: Scope?): Boolean = (value == null)

    override fun release(scope: Scope?) {}
}