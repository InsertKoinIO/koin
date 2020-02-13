package org.koin.test

import org.junit.Assert.assertNull
import org.koin.core.context.KoinContextHandler

fun assertHasNoStandaloneInstance() {
    assertNull(KoinContextHandler.getOrNull())
}