package org.koin.test.core.instance

import org.koin.core.Koin
import org.koin.core.instance.Instance
import org.koin.core.instance.InstanceHolder
import org.koin.core.parameter.ParameterDefinition
import org.koin.dsl.definition.BeanDefinition
import org.koin.error.BeanInstanceCreationException
import org.koin.error.DependencyResolutionException
import org.koin.error.NoBeanDefFoundException
import org.koin.test.error.BrokenDefinitionException
import org.mockito.Mockito

class SandboxedInstanceHolder<T>(override val bean: BeanDefinition<T>) : InstanceHolder<T> {

    private var instance: T? = null

    override fun <T> get(parameters: ParameterDefinition): Instance<T> = Instance(create(parameters), true)

    override fun delete() {
        instance = null
    }

    /**
     * Create Sandbox instance
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T> create(parameters: ParameterDefinition): T {
        try {
            val parameterList = parameters()
            bean.definition.invoke(parameterList)
        } catch (e: Exception) {
            when (e) {
                is NoBeanDefFoundException, is DependencyResolutionException, is BeanInstanceCreationException ->
                    throw BrokenDefinitionException("Definition $bean is broken due to error : $e")
                else -> Koin.logger.err("sandbox ~ intercepted error : $e")
            }
        }
        val clazz = bean.clazz.java
        return Mockito.mock(clazz) as T
    }
}