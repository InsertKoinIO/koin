package org.koin.test

import org.koin.mp.PlatformTools
import kotlin.test.assertNull

fun assertHasNoStandaloneInstance() {
    assertNull(PlatformTools.defaultContext().getOrNull())
}