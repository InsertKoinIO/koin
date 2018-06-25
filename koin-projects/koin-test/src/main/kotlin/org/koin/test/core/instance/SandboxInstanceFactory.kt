package org.koin.test.core.instance

import org.koin.core.Koin
import org.koin.core.instance.InstanceFactory
import org.koin.core.parameter.ParameterDefinition
import org.koin.dsl.definition.BeanDefinition
import org.koin.error.BeanInstanceCreationException
import org.koin.error.DependencyResolutionException
import org.koin.error.NoBeanDefFoundException
import org.koin.test.error.BrokenDefinitionException
import org.mockito.Mockito.mock

@Suppress("UNCHECKED_CAST")
class SandboxInstanceFactory() : InstanceFactory() {

    /**
     * Create Sandbox instance
     */
    override fun <T> createInstance(def: BeanDefinition<*>, p: ParameterDefinition): T {
        try {
            val parameterList = p()
            def.definition.invoke(parameterList)
        } catch (e: Exception) {
            when (e) {
                is NoBeanDefFoundException, is DependencyResolutionException, is BeanInstanceCreationException ->
                    throw BrokenDefinitionException("Definition $def is broken due to error : $e")
                else -> Koin.logger.err("sandbox ~ $e")
            }
        }
        val clazz = def.clazz.java
//        Koin.logger.info("|--['$clazz']")
        return mock(clazz) as T
    }

}