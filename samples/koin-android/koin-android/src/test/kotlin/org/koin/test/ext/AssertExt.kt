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

fun KoinContext.assertScopeInstances(scopeName: String, size: Int) {
    val scope = getScope(scopeName)
    val definitions = AllDefinitions().filter { it.value == scope }.map { it.key }.toSet()
    val instances = allInstances().filter { it.first in definitions }
    Assert.assertEquals("scope $scopeName must have $size instances", size, instances.size)
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