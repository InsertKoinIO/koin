package org.koin.test.android.helper

import android.content.ComponentCallbacks
import android.content.res.Configuration
import org.koin.core.component.KoinComponent
import org.koin.core.qualifier.named
import org.koin.dsl.module

object Helper {
    val koinComponent: KoinComponent = object : ComponentCallbacks, KoinComponent {
        override fun onConfigurationChanged(newConfig: Configuration) = Unit
        override fun onLowMemory() = Unit
    }

    val componentCallbacks = object : ComponentCallbacks {
        override fun onConfigurationChanged(newConfig: Configuration) = Unit
        override fun onLowMemory() = Unit
    }

    val module = module {
        single<FakeContent>(named("custom")) { FakeContentImpl() }
        single<FakeContent> { FakeContentImpl() }
    }
}
