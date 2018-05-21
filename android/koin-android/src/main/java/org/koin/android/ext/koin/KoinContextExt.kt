package org.koin.android.ext.koin

import org.koin.core.KoinContext
import org.koin.standalone.StandAloneContext

/**
 * Help work on context
 */
internal fun context(): KoinContext = (StandAloneContext.koinContext as KoinContext)