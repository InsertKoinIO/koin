package com.example.app

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

// The page targets Android and iOS. This actual lets the graph run on the JVM.
@Module
@ComponentScan
actual class PlatformModule

@Singleton
class JvmComponent : PlatformComponent { override fun getInfo() = "JVM" }

@KoinApplication(modules = [PlatformModule::class])
object PageApp
