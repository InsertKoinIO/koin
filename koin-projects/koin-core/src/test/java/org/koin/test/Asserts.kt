package org.koin.test

import org.junit.Assert.assertNull
import org.koin.core.context.GlobalContext

fun assertHasNoStandaloneInstance() {
    assertNull(GlobalContext.getOrNull())
}