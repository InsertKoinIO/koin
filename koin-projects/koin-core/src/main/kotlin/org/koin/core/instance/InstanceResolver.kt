///*
// * Copyright 2017-2018 the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package org.koin.core.instance
//
//import org.koin.core.KoinApplication.Companion.logger
//import org.koin.core.bean.BeanDefinition
//import org.koin.core.parameter.ParametersDefinition
//import org.koin.core.scope.Scope
//import java.util.*
//
///**
// * DefaultInstance Resolve
// * make instance resolution
// */
//class InstanceResolver {
//
//    private val callStack = Stack<BeanDefinition<*>>()
//
//    /**
//     * Resolve an instance
//     * @param definition
//     * @param scope
//     * @param parameters
//     */
//    fun <T> resolveInstance(
//        definition: BeanDefinition<*>,
//        scope: Scope?,
//        parameters: ParametersDefinition?
//    ): T {
////        checkForCycle(definition)
////
////        prepareCallStack(definition)
////
////        val instance: T =
////
////        cleanCallStack(definition)
//        return getInstance(definition, scope, parameters)
//    }
//
//    /**
//     * prepare resolution call stack
//     */
//    private fun prepareCallStack(definition: BeanDefinition<*>?) {
//        callStack.add(definition)
//    }
//
//    /**
//     * Retrieve instance
//     * @param definition
//     * @param scope
//     * @param parameters
//     */
//    private fun <T> getInstance(
//        definition: BeanDefinition<*>,
//        scope: Scope?,
//        parameters: ParametersDefinition?
//    ): T {
//        return definition.instance.get(scope, parameters)
//    }
//
//    private fun lastInStack() = if (callStack.isNotEmpty()) callStack.pop() else null
//
//    /**
//     * Clean call stack from definition call
//     * @param definition
//     */
//    private fun cleanCallStack(definition: BeanDefinition<*>?) {
//        val pop = lastInStack()
//        if (pop != definition) {
//            logger.error("call stack is inconsistent: return with $pop & should be $definition")
//            error("CallStack integrity error while resolving $definition")
//        }
//    }
//
//    /**
//     * Check call stack for any cycle dependency
//     * @param definition
//     */
//    private fun checkForCycle(definition: BeanDefinition<*>?) {
//        if (callStack.any { it == definition }) {
//            val pop = lastInStack()
//            logger.error("cycle dependency detected for $definition & $pop")
//            error("CallStack cycle detected for $definition & $pop")
//        }
//    }
//
//    /**
//     * Close resources
//     */
//    fun close() {
//        callStack.clear()
//    }
//}