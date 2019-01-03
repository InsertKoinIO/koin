/*
 * Copyright 2017-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.koin.test.check

import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.definition.BeanDefinition
import org.koin.core.parameter.emptyParametersHolder
import org.koin.core.scope.getScopeName

/**
 * Check all definition's dependencies - run all modules in a test sandbox
 * and checkModules if definitions can run
 */
fun KoinApplication.checkModules() = koin.checkModules()

/**
 * Check all definition's dependencies - run all modules in a test sandbox
 * and checkModules if definitions can run
 */
fun Koin.checkModules() {
    val allDefinitions = getSandboxedDefinitions()

    registerDefinitions(allDefinitions)

    runDefinitions(allDefinitions)

    close()
}

/**
 * Resolve & instance definitions
 */
fun Koin.runDefinitions(allDefinitions: List<BeanDefinition<*>>) {
    allDefinitions.forEach {
        val clazz = it.primaryType
        val scope = if (it.isScoped()) scopeRegistry.createScopeInstance(
                it.getScopeName() ?: error("definition $it should have a scope id")
        ) else null

        get<Any>(clazz, it.name, scope) { emptyParametersHolder() }
        scope?.let { scope.close() }
    }
}

private fun Koin.registerDefinitions(allDefinitions: List<BeanDefinition<*>>) {
    allDefinitions.forEach {
        beanRegistry.saveDefinition(it)
    }
}

private fun Koin.getSandboxedDefinitions(): List<BeanDefinition<*>> {
    return beanRegistry.getAllDefinitions()
            .map {
                KoinApplication.logger.debug("* sandbox for $it")
                it.cloneForSandbox() as BeanDefinition<*>
            }
}

/**
 * Clone definition and inject SandBox instance holder
 */
fun <T> BeanDefinition<T>.cloneForSandbox(): BeanDefinition<T> {
    val copy = this.copy()
    copy.secondaryTypes = this.secondaryTypes
    copy.instance = SandboxInstance(copy)
    copy.definition = definition
    copy.attributes = this.attributes.copy()
    copy.options = this.options.copy()
    copy.options.override = true
    copy.kind = this.kind
    return copy
}