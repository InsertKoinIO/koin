package org.koin.android.ext.android

import android.content.ComponentCallbacks
import org.koin.core.Koin
import org.koin.core.context.GlobalContext


/**
 * Get Koin context
 */
fun ComponentCallbacks.getKoin(): Koin = GlobalContext.get().koin