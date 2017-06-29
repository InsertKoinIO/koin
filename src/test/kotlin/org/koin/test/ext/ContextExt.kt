package org.koin.test.ext

import org.junit.Assert
import org.koin.KoinContext

/**
 * Context Test Utils
 */

fun KoinContext.definitions() = beanRegistry.definitions

fun KoinContext.allContext() = instanceResolver.all_context

fun KoinContext.instances() = allContext().flatMap { it.value.instances.toList() }

fun KoinContext.properties() = propertyResolver.registry.properties

fun KoinContext.assertSizes(definitionSize: Int, instanceSize: Int) {
    Assert.assertEquals("context definition size must be equals", definitionSize, definitions().size)
    Assert.assertEquals("context instances size must be equals", instanceSize, instances().size)
}

fun KoinContext.assertProps(properties: Int) {
    Assert.assertEquals("context properties size must be equals", properties, properties().size)
}

fun KoinContext.assertScopes(scopeSize: Int) {
    Assert.assertEquals("context scope size must be equals", scopeSize, allContext().size)
}