package org.koin.android

import org.koin.KoinContext

/**
 * Component got a Koin Context
 */
interface KoinContextAware {

    /**
     * Get actual context
     */
    fun getKoin(): KoinContext
}