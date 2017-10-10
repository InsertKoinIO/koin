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
    Assert.assertEquals("$definitionClazz must be in getScope $scopeName", beanRegistry.getScope(scopeName), beanRegistry.definitions[definition])
}

fun KoinContext.assertScopeParent(scopeName: String, scopeParent: String) {
    Assert.assertEquals("Scope '$scopeName' must have parent '$scopeName'", scopeParent, beanRegistry.getScope(scopeName).parent?.name)
}

fun KoinContext.assertInstances(instanceSize: Int) {
    Assert.assertEquals("context allInstances size must be equals", instanceSize, allInstances().size)
}

//fun KoinContext.assertProps(properties: Int) {
//    Assert.assertEquals("context allProperties size must be equals", properties, allProperties().size)
//}

fun KoinContext.assertContexts(totalContexts: Int) {
    Assert.assertEquals("applicationContext must have $totalContexts contexts", totalContexts, allContext().size)
}

//fun KoinContext.assertContexts(getScope: KClass<*>, size: Int) {
//    Assert.assertEquals("context getScope $getScope must be equals", size, getScopeInstances(getScope).size)
//}
//
//fun KoinContext.assertRootScope(size: Int) {
//    Assert.assertEquals("context ROOT has $size instances", size, getScopeInstances(RootScope::class).size)
//}
//
//fun KoinContext.assertParentScope(parent: Scope, source: Scope) {
//    Assert.assertEquals("getScope $parent must be parent of $source", parent, source.parentScope)
//}