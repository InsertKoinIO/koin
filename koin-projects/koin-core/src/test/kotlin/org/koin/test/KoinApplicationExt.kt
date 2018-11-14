package org.koin.test

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.koin.core.KoinApplication
import org.koin.core.bean.BeanDefinition
import org.koin.core.scope.Scope
import kotlin.reflect.KClass

fun KoinApplication.assertDefinitionsCount(count: Int) {
    assertEquals("definitions count", count, this.koin.beanRegistry.size())
}

fun KoinApplication.assertScopeHasBeenCreated(scope: Scope) {
    val id = scope.id
    assertTrue("Scope $id should be created", koin.scopeRegistry.getScopeById(id) != null)
}

internal fun KoinApplication.getDefinition(clazz: KClass<*>): BeanDefinition<*>? {
    return this.koin.beanRegistry.getDefinition(clazz)
}