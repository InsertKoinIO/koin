package org.koin.test

import org.junit.Assert
import org.junit.Assert.assertEquals
import org.koin.core.KoinApplication
import org.koin.core.standalone.StandAloneKoinApplication


fun KoinApplication.assertDefinitionsCount(count: Int) {
    assertEquals("definitions count", count, this.koin.beanRegistry.definitions.size)
}

fun assertHasNoStandaloneInstance() {
    try {
        StandAloneKoinApplication.get()
        Assert.fail()
    } catch (e: Exception) {
    }
}