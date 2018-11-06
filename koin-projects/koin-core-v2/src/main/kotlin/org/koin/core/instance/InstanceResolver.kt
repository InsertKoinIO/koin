package org.koin.core.instance

import org.koin.core.KoinApplication.Companion.logger
import org.koin.core.bean.BeanDefinition
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.scope.Scope
import java.util.*

class InstanceResolver {

    private val callStack = Stack<BeanDefinition<*>>()

    inline fun <reified T> resolveInstance(
        definition: BeanDefinition<*>,
        scope: Scope?,
        noinline parameters: ParametersDefinition?
    ): T {
        checkForCycle(definition)

        prepareCallStack(definition)

        val instance: T = getInstance(definition, scope, parameters)

        cleanCallStack(definition)
        return instance
    }

    fun prepareCallStack(definition: BeanDefinition<*>?) {
        callStack.add(definition)
    }

    inline fun <reified T> getInstance(
        definition: BeanDefinition<*>,
        scope: Scope?,
        noinline parameters: ParametersDefinition?
    ): T {
        return definition.instance.get(scope, parameters)
    }

    private fun lastInStack() = if (callStack.isNotEmpty()) callStack.pop() else null

    fun cleanCallStack(definition: BeanDefinition<*>?) {
        val pop = lastInStack()
        if (pop != definition) {
            logger.error { "call stack is inconsistent: return with $pop & should be $definition" }
            error("CallStack integrity error while resolving $definition")
        }
    }

    fun checkForCycle(definition: BeanDefinition<*>?) {
        if (callStack.any { it == definition }) {
            val pop = lastInStack()
            logger.error { "cycle dependency detected for $definition & $pop" }
            error("CallStack cycle detected for $definition & $pop")
        }
    }

    fun close() {
        callStack.clear()
    }
}