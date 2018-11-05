package org.koin.test

import org.junit.Assert
import org.junit.Assert.assertEquals
import org.koin.core.KoinApplication
import org.koin.core.bean.BeanDefinition
import org.koin.core.standalone.StandAloneKoinApplication
import kotlin.reflect.KClass

fun BeanDefinition<*>.hasBeenCreated() = this.instance.isAlreadyCreated()

fun KoinApplication.assertDefinitionsCount(count: Int) {
    assertEquals("definitions count", count, this.koin.beanRegistry.definitions.size)
}

fun KoinApplication.getDefinition(clazz: KClass<*>): BeanDefinition<*>? {
    return this.koin.beanRegistry.definitions.firstOrNull { it.primaryType == clazz || it.secondaryTypes.contains(clazz) }
}

fun assertHasNoStandaloneInstance() {
    try {
        StandAloneKoinApplication.get()
        Assert.fail()
    } catch (e: Exception) {
    }
}