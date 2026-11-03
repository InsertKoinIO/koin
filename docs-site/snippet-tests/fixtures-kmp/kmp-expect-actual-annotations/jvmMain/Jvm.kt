package com.example.app

import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Singleton

@Singleton
actual class NativeComponent actual constructor() {
    actual fun getInfo() = "JVM"
}

@KoinApplication(modules = [NativeComponentModule::class])
object PageApp
