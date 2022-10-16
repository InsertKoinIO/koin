package org.koin.test.android.helper

import android.content.ComponentCallbacks
import android.content.res.Configuration
import org.koin.core.component.KoinComponent

object Helper {
    val koinComponent: KoinComponent = object : ComponentCallbacks, KoinComponent {
        override fun onConfigurationChanged(newConfig: Configuration) = Unit
        override fun onLowMemory() = Unit
    }

    val componentCallbacks = object : ComponentCallbacks {
        override fun onConfigurationChanged(newConfig: Configuration) = Unit
        override fun onLowMemory() = Unit
    }
}
