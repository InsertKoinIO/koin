package org.koin.test.ext

import org.junit.Assert
import org.koin.KoinContext
import kotlin.reflect.KClass

/**
 * Asserts tools
 */

fun KoinContext.assertDefinitions(definitionSize: Int) {
    Assert.assertEquals("applicationContext must have $definitionSize definition", definitionSize, AllDefinitions().size)
}

fun KoinContext.assertDefinedInScope(definitionClazz: KClass<*>, scopeName: String) {
    val definition = definition(definitionClazz)
    Assert.assertEquals("$definitionClazz must be in getScopeForDefinition $scopeName", beanRegistry.getScope(scopeName), beanRegistry.definitions[definition])
}

fun KoinContext.assertScopeParent(scopeName: String, scopeParent: String) {
    Assert.assertEquals("Scope '$scopeName' must have parent '$scopeName'", scopeParent, beanRegistry.getScope(scopeName).parent?.name)
}

fun KoinContext.assertRemainingInstances(instanceSize: Int) {
    Assert.assertEquals("context must have $instanceSize instances", instanceSize, allInstances().size)
}

fun KoinContext.assertProperties(properties: Int) {
    Assert.assertEquals("context must have $properties properties", properties, allProperties().size)
}

fun KoinContext.assertContexts(totalContexts: Int) {
    Assert.assertEquals("context must have $totalContexts contexts", totalContexts, allContext().size)
}