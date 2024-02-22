package org.koin.test

import org.koin.mp.KoinPlatformTools
import kotlin.test.assertNull

fun assertHasNoStandaloneInstance() {
    assertNull(KoinPlatformTools.defaultContext().getOrNull())
}
