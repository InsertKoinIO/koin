---
title: Kotlin Multiplatform
---

## Setup

The Koin Compiler Plugin simplifies KMP setup - just apply the plugin.

```kotlin
// shared/build.gradle.kts
plugins {
    kotlin("multiplatform")
    alias(libs.plugins.koin.compiler)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.koin.annotations)
        }
    }
}
```

That's it! No per-platform KSP configuration needed.

## Defining Definitions and Modules in Common Code

In your `commonMain` sourceSet, declare your Module, scan for definitions, or define functions as regular Kotlin Koin declarations. See [Definitions](./definitions) and [Modules](./modules).

## Sharing Patterns

In this section, we will see together several ways to share components with definitions and modules.

In a Kotlin Multiplatform application, some components must be implemented specifically per platform. You can share those components at the definition level, with expected/actual on the given class (definition or module).
You can share a definition with expect/actual implementation, or a module with expect/actual.

:::info
Please look at [Multiplatform Expect & Actual Rules](https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-expect-actual.html) documentation for general Kotlin guidance.
:::

:::warning
Expect/Actual classes can't have different constructors per platform. You need to respect the current constructor contract designed in common space
:::

### Sharing Definitions for native implementations

:::info
We target sharing with a Common Module + Expect/Actual Class Definition
:::

For this first classic pattern, you can use both definitions scanning with `@ComponentScan` or declare a definition as a module class function.

Be aware that to use `expect/actual` definitions, you will use the same constructor (either the default or a custom one). This constructor has to be the same on all platforms.  

#### Scanning for Expect/Actual definitions

In commonMain:
```kotlin
// commonMain

@Module
@ComponentScan("com.jetbrains.kmpapp.native")
class NativeModuleA()

// package com.jetbrains.kmpapp.native
@Factory
expect class PlatformComponentA() {
    fun sayHello() : String
}
```

In native sources, implement our actual classes:

```kotlin
// androidMain

// package com.jetbrains.kmpapp.native
actual class PlatformComponentA {
    actual fun sayHello() : String = "I'm Android - A"
}

// iOSMain

// package com.jetbrains.kmpapp.native
actual class PlatformComponentA {
    actual fun sayHello() : String = "I'm iOS - A"
}
```

#### Declaring Expect/Actual function definitions

In commonMain:
```kotlin
// commonMain

@Module
class NativeModuleB() {

    @Factory
    fun providesPlatformComponentB() : PlatformComponentB = PlatformComponentB()
}

expect class PlatformComponentB() {
    fun sayHello() : String
}
```

In native sources, implement our actual classes:

```kotlin
// androidMain

// package com.jetbrains.kmpapp.native
actual class PlatformComponentB {
    actual fun sayHello() : String = "I'm Android - B"
}

// iOSMain

// package com.jetbrains.kmpapp.native
actual class PlatformComponentB {
    actual fun sayHello() : String = "I'm iOS - B"
}
```

### Sharing Definitions with different native contracts

:::info
We target Expect/Actual common Module + common Interface + native implementations
:::

In some cases, you need different constructor arguments on each native implementation. Then Expect/Actual class is not your solution. 
You need to go with an `interface` to implement on each platform, and a Expect/Actual class module to allow a module to define your right platform implementation:

In commonMain:
```kotlin
// commonMain

expect class NativeModuleD() {
    @Factory
    fun providesPlatformComponentD(scope : org.koin.core.scope.Scope) : PlatformComponentD
}

interface PlatformComponentD {
    fun sayHello() : String
}
```

In native sources, implement our actual classes:

```kotlin
// androidMain

@Module
actual class NativeModuleD {
    @Factory
    actual fun providesPlatformComponentD(scope : org.koin.core.scope.Scope) : PlatformComponentD = PlatformComponentDAndroid(scope)
}

class PlatformComponentDAndroid(scope : org.koin.core.scope.Scope) : PlatformComponentD{
    val context : Context = scope.get()
    override fun sayHello() : String = "I'm Android - D - with ${context}"
}

// iOSMain
@Module
actual class NativeModuleD {
    @Factory
    actual fun providesPlatformComponentD(scope : org.koin.core.scope.Scope) : PlatformComponentD = PlatformComponentDiOS()
}

class PlatformComponentDiOS : PlatformComponentD{
    override fun sayHello() : String = "I'm iOS - D"
}
```

:::note
Each time you use manual access to Koin scope, you are doing dynamic wiring. Compile safety doesn't cover such wiring.
:::

### Safely Sharing across platforms with Platform Wrapper

:::info
Wrap a specific platform component, as a "platform wrapper"
:::

You can wrap a specific platform component, as a "platform wrapper", to help you minimize dynamic injection.

For example, we can do a `ContextWrapper` that lets us inject Android `Context` when needed, but doesn't impact the iOS side.

In commonMain:
```kotlin
// commonMain

expect class ContextWrapper

@Module
expect class ContextModule() {

    @Single
    fun providesContextWrapper(scope : Scope) : ContextWrapper
}
```

In native sources, implement our actual classes:

```kotlin
// androidMain
actual class ContextWrapper(val context: Context)

@Module
actual class ContextModule {
    
    // needs androidContext() to be setup at start
    @Single
    actual fun providesContextWrapper(scope : Scope) : ContextWrapper = ContextWrapper(scope.get())
}

// iOSMain
actual class ContextWrapper

@Module
actual class ContextModule {

    @Single
    actual fun providesContextWrapper(scope : Scope) : ContextWrapper = ContextWrapper()
}
```

:::info
This way, you minimize the dynamic platform wiring to one definition, and inject safely in your entire system.
:::

You can now use your `ContextWrapper` from common code, and easily pass it in your Expect/Actual classes:

In commonMain:
```kotlin
// commonMain

@Module
@ComponentScan("com.jetbrains.kmpapp.native")
class NativeModuleA()

// package com.jetbrains.kmpapp.native
@Factory
expect class PlatformComponentA(ctx : ContextWrapper) {
    fun sayHello() : String
}
```

In native sources, implement our actual classes:

```kotlin
// androidMain

// package com.jetbrains.kmpapp.native
actual class PlatformComponentA actual constructor(val ctx : ContextWrapper) {
    actual fun sayHello() : String = "I'm Android - A - with context: ${ctx.context}"
}

// iOSMain

// package com.jetbrains.kmpapp.native
actual class PlatformComponentA actual constructor(val ctx : ContextWrapper) {
    actual fun sayHello() : String = "I'm iOS - A"
}
```

### Sharing Expect/Actual Module - rely on Native Module Scanning

:::info
Rely on a native module from a common module 
:::

In some cases, you don't want to have constraints, and scan for components on each native side. Define an empty module class in the common source set, and define your implementation on each platform.

:::info
If you define an empty module in the common side, each native module implementation will be generated from each native target, allowing to scan native only components for example.
:::

In commonMain:
```kotlin
// commonMain

@Module
expect class NativeModuleC()
```

In native source sets:

```kotlin
// androidMain
@Module
@ComponentScan("com.jetbrains.kmpapp.other.android")
actual class NativeModuleC

//com.jetbrains.kmpapp.other.android
@Factory
class PlatformComponentC(val context: Context) {
    fun sayHello() : String = "I'm Android - C - $context"
}

// iOSMain
// do nothing on iOS
@Module
actual class NativeModuleC
```