package org.koin.test.ext.junit

import org.junit.Assert
import org.koin.KoinContext
import org.koin.standalone.StandAloneContext
import org.koin.test.KoinTest
import org.koin.test.ext.koin.*
import kotlin.reflect.KClass

/**
 * Assert context definition definitionCount
 * @param definitionCount - number of definitions
 */
fun KoinTest.assertDefinitions(definitionCount: Int) {
    Assert.assertEquals("applicationContext must have $definitionCount definition", definitionCount, (StandAloneContext.koinContext as KoinContext).AllDefinitions().size)
}

/**
 * Assert definitionClazz is defined in given scope
 * @param definitionClazz - bean definition class
 * @param scopeName - scope name
 */
fun KoinTest.assertDefinedInScope(definitionClazz: KClass<*>, scopeName: String) {
    val definition = (StandAloneContext.koinContext as KoinContext).definition(definitionClazz)
    Assert.assertEquals("$definitionClazz must be in scope '$scopeName'", (StandAloneContext.koinContext as KoinContext).beanRegistry.getScope(scopeName), (StandAloneContext.koinContext as KoinContext).beanRegistry.definitions[definition])
}

/**
 * Assert context has definition instanceCount
 * @param scopeName - scope name
 * @param instanceCount - number of instances
 */
fun KoinTest.assertContextInstances(scopeName: String, instanceCount: Int) {
    val scope = (StandAloneContext.koinContext as KoinContext).getScope(scopeName)
    val definitions = (StandAloneContext.koinContext as KoinContext).AllDefinitions().filter { it.value == scope }.map { it.key }.toSet()
    val instances = (StandAloneContext.koinContext as KoinContext).allInstances().filter { it.first in definitions }
    Assert.assertEquals("scope $scopeName must have $instanceCount instances", instanceCount, instances.size)
}

/**
 * Assert scope has given parent scope
 * @param scopeName - target scope name
 * @param scopeParent - parent scope name
 */
fun KoinTest.assertScopeParent(scopeName: String, scopeParent: String) {
    Assert.assertEquals("Scope '$scopeName' must have parent '$scopeName'", scopeParent, (StandAloneContext.koinContext as KoinContext).beanRegistry.getScope(scopeName).parent?.name)
}

/**
 * Assert Koin has reminaing instances
 * @param instanceCount - instances count
 */
fun KoinTest.assertRemainingInstances(instanceCount: Int) {
    Assert.assertEquals("context must have $instanceCount instances", instanceCount, (StandAloneContext.koinContext as KoinContext).allInstances().size)
}

/**
 * Assert Koin has properties count
 * @param propertyCount - properties count
 */
fun KoinTest.assertProperties(propertyCount: Int) {
    Assert.assertEquals("context must have $propertyCount properties", propertyCount, (StandAloneContext.koinContext as KoinContext).allProperties().size)
}

/**
 * Assert Koin has contextCount contexts
 * @param contextCount - context count
 */
fun KoinTest.assertContexts(contextCount: Int) {
    Assert.assertEquals("context must have $contextCount contexts", contextCount, (StandAloneContext.koinContext as KoinContext).allContext().size)
}