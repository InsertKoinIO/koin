package org.koin.test

import org.junit.Assert
import org.koin.core.standalone.StandAloneKoinApplication


fun assertHasNoStandaloneInstance() {
    try {
        StandAloneKoinApplication.get()
        Assert.fail()
    } catch (e: Exception) {
    }
}