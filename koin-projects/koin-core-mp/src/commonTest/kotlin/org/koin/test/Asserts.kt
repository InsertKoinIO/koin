package org.koin.test

import org.koin.core.context.KoinContextHandler
import kotlin.test.assertNull

fun assertHasNoStandaloneInstance() {
    assertNull(KoinContextHandler.getOrNull())
}