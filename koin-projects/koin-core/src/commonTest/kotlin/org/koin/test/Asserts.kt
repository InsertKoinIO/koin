package org.koin.test

import org.koin.core.context.GlobalContext
import kotlin.test.assertNull

fun assertHasNoStandaloneInstance() {
    assertNull(GlobalContext.getOrNull())
}
