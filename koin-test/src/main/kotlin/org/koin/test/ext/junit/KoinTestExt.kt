package org.koin.test.ext.junit

import org.junit.Assert
import org.koin.core.KoinContext
import org.koin.standalone.StandAloneContext
import org.koin.test.KoinTest
import org.koin.test.ext.koin.*
import kotlin.reflect.KClass

internal fun context() = (StandAloneContext.koinContext as KoinContext)

/**
 * Assert context definition definitionCount
 * @param definitionCount - number of definitions
 */
fun KoinTest.assertDefinitions(definitionCount: Int) {
    Assert.assertEquals("applicationContext must have $definitionCount definition", definitionCount, context().AllDefinitions().size)
}

/**
 * Assert definitionClazz is defined in given scope
 * @param definitionClazz - bean definition class
 * @param scopeName - scope name
 */
fun KoinTest.assertDefinedInScope(definitionClazz: KClass<*>, scopeName: String) {
    val definition = context().definition(definitionClazz)
    Assert.assertEquals("$definitionClazz must be in scope '$scopeName'", scopeName, definition?.scope?.name ?: "")
}

/**
 * Assert context has definition instanceCount
 * @param scopeName - scope name
 * @param instanceCount - number of instances
 */
fun KoinTest.assertContextInstances(scopeName: String, instanceCount: Int) {
    val scope = context().getScope(scopeName)
    val definitions = context().AllDefinitions().filter { it.scope == scope }.toSet()
    val instances = context().allInstances().filter { it.first in definitions }
    Assert.assertEquals("scope $scopeName must have $instanceCount instances", instanceCount, instances.size)
}

/**
 * Assert scope has given parent scope
 * @param scopeName - target scope name
 * @param scopeParent - parent scope name
 */
fun KoinTest.assertScopeParent(scopeName: String, scopeParent: String) {
    Assert.assertEquals("Scope '$scopeName' must have parent '$scopeName'", scopeParent, context().beanRegistry.getScope(scopeName).parent?.name)
}

/**
 * Assert Koin has reminaing instances
 * @param instanceCount - instances count
 */
fun KoinTest.assertRemainingInstances(instanceCount: Int) {
    Assert.assertEquals("context must have $instanceCount instances", instanceCount, context().allInstances().size)
}

/**
 * Assert Koin has properties count
 * @param propertyCount - properties count
 */
fun KoinTest.assertProperties(propertyCount: Int) {
    val nonKoinProps = context().allProperties().filterKeys { it != "test.koin" && it != "os.version" }
    Assert.assertEquals("context must have $propertyCount properties", propertyCount, nonKoinProps.size)
}

/**
 * Assert Koin has contextCount contexts
 * @param contextCount - context count
 */
fun KoinTest.assertContexts(contextCount: Int) {
    Assert.assertEquals("context must have $contextCount contexts", contextCount, context().allContext().size)
}