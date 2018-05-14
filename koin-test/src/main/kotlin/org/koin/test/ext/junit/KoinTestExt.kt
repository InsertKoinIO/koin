package org.koin.test.ext.junit

import org.junit.Assert
import org.koin.core.KoinContext
import org.koin.standalone.StandAloneContext
import org.koin.test.KoinTest
import org.koin.test.ext.koin.*
import kotlin.reflect.KClass

internal fun context() = (StandAloneContext.koinContext as KoinContext)

/**
 * Assert context beanDefinition definitionCount
 * @param definitionCount - number of definitions
 */
fun KoinTest.assertDefinitions(definitionCount: Int) {
    Assert.assertEquals(
        "applicationContext must have $definitionCount beanDefinition",
        definitionCount,
        context().beanDefinitions().size
    )
}

/**
 * Assert definitionClazz is defined in given scope
 * @param definitionClazz - bean beanDefinition class
 * @param scopeName - scope name
 */
fun KoinTest.assertDefinedInScope(definitionClazz: KClass<*>, scopeName: String) {
    val definition = context().beanDefinition(definitionClazz)
    Assert.assertEquals("$definitionClazz must be in scope '$scopeName'", scopeName, definition?.modulePath?.name ?: "")
}

/**
 * Assert context has beanDefinition instanceCount
 * @param scopeName - scope name
 * @param instanceCount - number of instances
 */
fun KoinTest.assertContextInstances(scopeName: String, instanceCount: Int) {
    val scope = context().getScope(scopeName)
    val definitions = context().beanDefinitions().filter { it.modulePath == scope }.toSet()
    val instances = context().allInstances().filter { it.first in definitions }
    Assert.assertEquals("scope $scopeName must have $instanceCount instances", instanceCount, instances.size)
}

/**
 * Assert path has given parent path
 * @param path - target scope name
 * @param parentPath - parent scope name
 */
fun KoinTest.assertScopeParent(path: String, parentPath: String) {
    Assert.assertEquals(
        "Scope '$path' must have parent '$path'",
        parentPath,
        context().pathRegistry.getPath(path).parent?.name
    )
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
    Assert.assertEquals("context must have $contextCount contexts", contextCount, context().allScopes().size)
}