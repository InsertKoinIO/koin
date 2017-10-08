package org.koin.test.ext

import org.junit.Assert
import org.koin.KoinContext
import org.koin.core.scope.RootScope
import org.koin.core.scope.Scope
import kotlin.reflect.KClass

/**
 * Asserts tools
 */

fun KoinContext.assertDefinitions(definitionSize: Int) {
    Assert.assertEquals("context definition size must be equals", definitionSize, AllDefinitions().size)
}

fun KoinContext.assertInstances(instanceSize: Int) {
    Assert.assertEquals("context allInstances size must be equals", instanceSize, allInstances().size)
}

fun KoinContext.assertProps(properties: Int) {
    Assert.assertEquals("context allProperties size must be equals", properties, allProperties().size)
}

fun KoinContext.assertScopes(scopeSize: Int) {
    Assert.assertEquals("context scope size must be equals", scopeSize, allContext().size)
}

fun KoinContext.assertScopes(scope: KClass<*>, size: Int) {
    Assert.assertEquals("context scope $scope must be equals", size, getScopeInstances(scope).size)
}

fun KoinContext.assertRootScope(size: Int) {
    Assert.assertEquals("context scope ROOT must be equals", size, getScopeInstances(RootScope::class).size)
}

fun KoinContext.assertParentScope(parent: Scope, source: Scope) {
    Assert.assertEquals("scope $parent must be parent of $source", parent, source.parentScope)
}

fun KoinContext.assertVisible(scope: Scope, definitionType: KClass<*>) {
    val def = AllDefinitions().keys.first { it.clazz == definitionType }
    Assert.assertTrue("definition $definitionType must have visibility on $scope", beanRegistry.isVisible(def, scope))
}

fun KoinContext.assertNotVisible(scope: Scope, definitionType: KClass<*>) {
    val def = AllDefinitions().keys.first { it.clazz == definitionType }
    Assert.assertFalse("definition $definitionType must not have visibility on $scope", beanRegistry.isVisible(def, scope))
}