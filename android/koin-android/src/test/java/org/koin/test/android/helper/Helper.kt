package org.koin.test.android.helper

import android.content.ComponentCallbacks
import android.content.res.Configuration
import org.koin.android.scope.AndroidScopeComponent
import org.koin.android.scope.createScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.KoinScopeComponent
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module

object Helper {

    val androidScopeComponent: AndroidScopeComponent =
        object : ComponentCallbacks, AndroidScopeComponent {
            override var scope: Scope? = createScope("AndroidScopeComponent")
            override fun onConfigurationChanged(newConfig: Configuration) = Unit
            override fun onLowMemory() = Unit
        }

    val koinScopeComponent: KoinScopeComponent = object : ComponentCallbacks, KoinScopeComponent {
        override var scope: Scope = createScope("KoinScopeComponent")
        override fun onConfigurationChanged(newConfig: Configuration) = Unit
        override fun onLowMemory() = Unit
    }

    val koinComponent: KoinComponent = object : ComponentCallbacks, KoinComponent {
        override fun onConfigurationChanged(newConfig: Configuration) = Unit
        override fun onLowMemory() = Unit
    }

    val componentCallbacks: ComponentCallbacks = object : ComponentCallbacks {
        override fun onConfigurationChanged(newConfig: Configuration) = Unit
        override fun onLowMemory() = Unit
    }

    val module = module {
        single<FakeContent>(named("custom")) { FakeContentImpl() }
        single<FakeContent> { FakeContentImpl() }
    }
}
