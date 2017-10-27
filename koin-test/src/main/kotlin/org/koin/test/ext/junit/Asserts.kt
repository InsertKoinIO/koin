package org.koin.test.ext.junit

import org.junit.Assert
import org.koin.KoinContext
import org.koin.test.ext.koin.*
import kotlin.reflect.KClass



/**
 * Assert context definition definitionCount
 * @param definitionCount - number of definitions
 */
fun KoinContext.assertDefinitions(definitionCount: Int) {
    Assert.assertEquals("applicationContext must have $definitionCount definition", definitionCount, AllDefinitions().size)
}

/**
 * Assert definitionClazz is defined in given scope
 * @param definitionClazz - bean definition class
 * @param scopeName - scope name
 */
fun KoinContext.assertDefinedInScope(definitionClazz: KClass<*>, scopeName: String) {
    val definition = definition(definitionClazz)
    Assert.assertEquals("$definitionClazz must be in scope '$scopeName'", beanRegistry.getScope(scopeName), beanRegistry.definitions[definition])
}

/**
 * Assert context has definition instanceCount
 * @param scopeName - scope name
 * @param instanceCount - number of instances
 */
fun KoinContext.assertContextInstances(scopeName: String, instanceCount: Int) {
    val scope = getScope(scopeName)
    val definitions = AllDefinitions().filter { it.value == scope }.map { it.key }.toSet()
    val instances = allInstances().filter { it.first in definitions }
    Assert.assertEquals("scope $scopeName must have $instanceCount instances", instanceCount, instances.size)
}

/**
 * Assert scope has given parent scope
 * @param scopeName - target scope name
 * @param scopeParent - parent scope name
 */
fun KoinContext.assertScopeParent(scopeName: String, scopeParent: String) {
    Assert.assertEquals("Scope '$scopeName' must have parent '$scopeName'", scopeParent, beanRegistry.getScope(scopeName).parent?.name)
}

/**
 * Assert Koin has reminaing instances
 * @param instanceCount - instances count
 */
fun KoinContext.assertRemainingInstances(instanceCount: Int) {
    Assert.assertEquals("context must have $instanceCount instances", instanceCount, allInstances().size)
}

/**
 * Assert Koin has properties count
 * @param propertyCount - properties count
 */
fun KoinContext.assertProperties(propertyCount: Int) {
    Assert.assertEquals("context must have $propertyCount properties", propertyCount, allProperties().size)
}

/**
 * Assert Koin has contextCount contexts
 * @param contextCount - context count
 */
fun KoinContext.assertContexts(contextCount: Int) {
    Assert.assertEquals("context must have $contextCount contexts", contextCount, allContext().size)
}