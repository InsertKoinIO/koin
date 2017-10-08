package org.koin.test.ext

import org.junit.Assert
import org.koin.KoinContext
import org.koin.core.scope.RootScope
import org.koin.core.scope.Scope
import kotlin.reflect.KClass

/**
 * Context Test Utils
 */

fun KoinContext.Alldefinitions() = beanRegistry.definitions

fun KoinContext.allContext() = instanceResolver.scopes

fun KoinContext.allInstances() = allContext().flatMap { it.instanceFactory.instances.toList() }

fun KoinContext.allProperties() = propertyResolver.registry.properties

fun KoinContext.getScope(scope: KClass<*>) = instanceResolver.scopes.first { it.clazz == scope }

fun KoinContext.getScopeInstances(scope: KClass<*>) = getScope(scope).instanceFactory.instances

inline fun <reified T> KoinContext.getOrNull(name: String = ""): T? {
    var instance: T? = null
    try {
        instance = if (name.isNotEmpty()) {
            this.get<T>(name)
        } else {
            this.get<T>()
        }
    } catch (e: Exception) {
    }
    return instance
}

fun KoinContext.rootScope() = instanceResolver.rootScope

fun KoinContext.assertDefinitions(definitionSize: Int) {
    Assert.assertEquals("context definition size must be equals", definitionSize, Alldefinitions().size)
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

fun KoinContext.assertScopeSize(scope: KClass<*>, size: Int) {
    Assert.assertEquals("context scope $scope must be equals", size, getScopeInstances(scope).size)
}

fun KoinContext.assertRootScopeSize(size: Int) {
    Assert.assertEquals("context scope ROOT must be equals", size, getScopeInstances(RootScope::class).size)
}

fun KoinContext.assertScopeIsParent(parent: Scope, target: Scope) {
    Assert.assertEquals(parent, target.parentScope)
}