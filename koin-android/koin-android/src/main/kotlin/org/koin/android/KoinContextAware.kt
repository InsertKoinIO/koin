package org.koin.android

import org.koin.KoinContext

/**
 * Holder of the Koin Context
 */
interface KoinContextAware {

    // The Koin Context
    val koinContext: KoinContext
}