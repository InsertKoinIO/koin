package com.jetbrains.kmpapp.platform

import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

// The page targets Android and iOS. This actual lets the graph run on the JVM.
class JvmComponent : PlatformComponent {
    override fun getInfo(): String = "JVM"
}

actual fun platformModule(): Module = module {
    single { JvmComponent() } bind PlatformComponent::class
}
