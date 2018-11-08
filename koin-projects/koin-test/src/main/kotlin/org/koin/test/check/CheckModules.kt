package org.koin.test.check

import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.bean.BeanDefinition
import org.koin.core.parameter.emptyParametersHolder
import org.koin.core.scope.getScopeId


/**
 * Check all definition's dependencies - run all modules in a test sandbox
 * and checkModules if definitions can run
 *
 * @param list of modules
 * @param logger - default is EmptyLogger
 */
fun KoinApplication.checkModules() = koin.checkModules()

fun Koin.checkModules() {
    val allDefinitions = getSandboxedDefinitions()

    registerDefinitions(allDefinitions)

    runDefinitions(allDefinitions)
}

private fun Koin.runDefinitions(allDefinitions: List<BeanDefinition<Any>>) {
    allDefinitions.forEach {
        val clazz = it.primaryType
        val scope = if (it.isScoped()) scopeRegistry.createScope(
            it.getScopeId() ?: error("definition $it should have a scope id")
        ) else null
        get(clazz, it.name, scope) { emptyParametersHolder() }
    }
}

private fun Koin.registerDefinitions(allDefinitions: List<BeanDefinition<Any>>) {
    allDefinitions.forEach {
        beanRegistry.saveDefinition(it)
    }
}

private fun Koin.getSandboxedDefinitions(): List<BeanDefinition<Any>> {
    val allDefinitions = beanRegistry.getDefinitions()
        .map {
            val beanDefinition = it as BeanDefinition<Any>
            beanDefinition.cloneForSandbox(SandboxInstance(beanDefinition))
        }
    return allDefinitions
}

fun <T> BeanDefinition<T>.cloneForSandbox(sandbox: SandboxInstance<T>): BeanDefinition<T> {
    val copy = this.copy()
    copy.secondaryTypes = this.secondaryTypes
    copy.instance = sandbox
    copy.definition = definition
    copy.attributes = this.attributes.copy()
    copy.options = this.options.copy()
    copy.kind = this.kind
    return copy
}