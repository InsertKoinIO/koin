package org.koin.test.ext.junit

import org.junit.Assert
import org.koin.core.KoinContext
import org.koin.dsl.path.Path
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
 * Assert definitionClazz is defined in given module path
 * @param definitionClazz - bean beanDefinition class
 * @param path
 */
fun KoinTest.assertIsInModulePath(definitionClazz: KClass<*>, path: String) {
    val definition = context().beanDefinition(definitionClazz)
    Assert.assertEquals("$definitionClazz must be in path '$path'", path, definition?.path?.name ?: "")
}

/**
 * Assert definitionClazz is defined in given module path
 * @param definitionClazz - bean beanDefinition class
 * @param path
 */
fun KoinTest.assertIsInRootPath(definitionClazz: KClass<*>) {
    val definition = context().beanDefinition(definitionClazz)
    Assert.assertEquals("$definitionClazz must be in path " + Path.ROOT, Path.ROOT, definition?.path?.name ?: "")
}


/**
 * Assert context has beanDefinition instanceCount
 * @param pathName
 * @param instanceCount - number of instances
 */
fun KoinTest.assertContextInstances(pathName: String, instanceCount: Int) {
    val path = context().getPath(pathName)
    val definitions = context().beanDefinitions().filter { it.path == path }.toSet()
    val instances = context().allInstances().filter { it.first in definitions }
    Assert.assertEquals("path $pathName must have $instanceCount instances", instanceCount, instances.size)
}

/**
 * Assert path has given parent path
 * @param path
 * @param parentPath
 */
fun KoinTest.assertPath(path: String, parentPath: String) {
    Assert.assertEquals(
        "Path '$path' must have parent '$parentPath'",
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
    Assert.assertEquals("context must have $contextCount contexts", contextCount, context().allPaths().size)
}