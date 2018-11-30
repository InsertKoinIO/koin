package org.koin.test

import org.junit.Assert.assertNull
import org.koin.core.standalone.StandAloneKoinApplication

fun assertHasNoStandaloneInstance() {
    assertNull(StandAloneKoinApplication.getOrNull())
}