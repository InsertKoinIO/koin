package org.koin.core.stack

import org.koin.dsl.definition.BeanDefinition
import org.koin.error.DependencyResolutionException
import java.util.*

/*
 * @author - Arnaud GIULIANI
 * @author - Laurent BARESSE
 */
class ResolutionStack {

    /**
     * call stack - bean definition resolution
     */
    private val stack = Stack<StackItem>()

    /**
     * Allow to execute the execution function if the bean definition is well defined on stack
     * @param beanDefinition - bean definition
     * @param execution - executed code once bean definition has been stacked
     */
    fun resolve(beanDefinition: BeanDefinition<*>, execution: () -> Unit) {
        checkStackEnter(beanDefinition)

        stack.add(beanDefinition)
        execution()

        checkStackExit(beanDefinition)
    }

    /**
     * Check if bean is not already on stack
     */
    @Throws(DependencyResolutionException::class)
    private fun checkStackEnter(beanDefinition: BeanDefinition<*>) {
        if (stack.any { it == beanDefinition }) {
            throw DependencyResolutionException(
                "Cyclic call while resolving $beanDefinition. Definition is already in resolution in current call:\n\t${stack.joinToString(
                    "\n\t"
                )}"
            )
        }
    }

    /**
     * Should pop the same bean definition after exit
     */
    @Throws(IllegalStateException::class)
    private fun checkStackExit(beanDefinition: BeanDefinition<*>) {
        val head: BeanDefinition<*> = stack.pop()
        if (head != beanDefinition) {
            stack.clear()
            throw DependencyResolutionException("Stack resolution error : was $head but should be $beanDefinition")
        }
    }

    /**
     * For log indentation
     */
    fun indent(): String = stack.joinToString(separator = "") { "\t" }

    /**
     * Last stack item
     */
    fun last(): StackItem? = if (stack.size > 0) stack.peek() else null

    /**
     * Clear stack
     */
    fun clear() = stack.clear()
}


/**
 * Resolution Stack Item
 */
typealias StackItem = BeanDefinition<*>