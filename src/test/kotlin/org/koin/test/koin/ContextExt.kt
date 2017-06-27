package org.koin.test.koin

import org.junit.Assert
import org.koin.Context

/**
 * Context Test Utils
 */

fun Context.definitions() = beanRegistry.definitions

fun Context.instances() = instanceFactory.instances

fun Context.properties() = propertyResolver.properties

fun Context.assertSizes(definitionSize: Int, instanceSize: Int) {
    Assert.assertEquals("context definition size must be equals", definitions().size, definitionSize)
    Assert.assertEquals("context instances size must be equals", instances().size, instanceSize)
}

fun Context.assertProps(properties: Int) {
    Assert.assertEquals("context properties size must be equals", properties().size, properties)
}