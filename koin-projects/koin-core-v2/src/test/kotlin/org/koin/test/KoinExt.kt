package org.koin.test

import org.junit.Assert
import org.junit.Assert.assertEquals
import org.koin.core.Koin
import org.koin.core.StandAlone


fun Koin.assertDefinitionsCount(count: Int) {
    assertEquals("definitions count", count, this.definitions.size)
}

fun assertHasNoStandaloneInstance() {
    try {
        StandAlone.getKoin()
        Assert.fail()
    } catch (e: Exception) {
    }
}