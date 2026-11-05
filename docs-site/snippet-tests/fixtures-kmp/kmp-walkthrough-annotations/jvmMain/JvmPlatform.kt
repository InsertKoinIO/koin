package com.jetbrains.kmpapp.platform

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

// The page targets Android and iOS. This actual lets the graph run on the JVM.
@Module
@ComponentScan
actual class PlatformComponentModule

@Singleton
class JvmPlatformComponent : PlatformComponent {
    override fun getInfo(): String = "JVM"
}
