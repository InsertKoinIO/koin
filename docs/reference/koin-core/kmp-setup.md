---
title: Kotlin Multiplatform Setup
description: Add Koin to a Kotlin Multiplatform project, write one shared start function, and call it from Android, iOS, desktop and web entry points.
---

This page sets up Koin in a Kotlin Multiplatform (KMP) project: Gradle dependencies, where the Koin code goes, one shared function that starts Koin, and how each platform calls it. It assumes you know [modules](/docs/reference/koin-core/modules) and [Starting Koin](/docs/reference/koin-core/starting-koin). How to give each platform its own implementations is covered in [KMP Sharing Patterns](/docs/reference/koin-core/kmp-shared-modules).

## Supported targets

`koin-core` and the Compiler Plugin support every Kotlin target:

| Target | Supported |
|--------|-----------|
| Android | Yes |
| iOS (`iosArm64`, `iosX64`, `iosSimulatorArm64`) | Yes |
| JVM (desktop, server) | Yes |
| JS and Wasm (`js`, `wasmJs`) | Yes |
| macOS, Linux, Windows (Kotlin/Native) | Yes |

Compose artifacts follow the targets of Compose Multiplatform. See the artifact table in [Gradle Setup](/docs/setup/gradle).

## Dependencies

Apply the [Koin Compiler Plugin](/docs/setup/compiler-plugin) to the shared module and add Koin to the source sets:

```kotlin
// shared/build.gradle.kts
plugins {
    kotlin("multiplatform")
    alias(libs.plugins.koin.compiler)
}

kotlin {
    androidTarget()
    iosArm64()
    iosSimulatorArm64()
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.annotations) // only with annotations

            // Compose Multiplatform
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
        }
        commonTest.dependencies {
            implementation(libs.koin.test)
        }
    }
}
```

The Compiler Plugin runs on every target from one declaration. There is no per-target configuration, unlike the KSP setup of Koin Annotations 1.x (see [Migrating from KSP](/docs/migration/from-ksp-to-compiler-plugin)).

## Project structure

```
shared/
└── src/
    ├── commonMain/kotlin/di/
    │   ├── Koin.kt              // modules and initKoin()
    │   └── PlatformModule.kt    // expect declaration
    ├── androidMain/kotlin/di/
    │   └── PlatformModule.android.kt
    └── iosMain/kotlin/di/
        └── PlatformModule.ios.kt
androidApp/   // Application class calls initKoin()
iosApp/       // Swift App calls initKoin()
```

Shared definitions live in `commonMain`. Each platform source set only contains what differs per platform.

## Shared start function

Write one function in `commonMain` that starts Koin. Each platform calls it, and can add its own configuration through the `config` parameter:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
// commonMain/kotlin/di/Koin.kt
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes
import org.koin.dsl.module
import org.koin.plugin.module.dsl.*

val dataModule = module {
    single<UserRepository>()
    viewModel<UserViewModel>()
}

val appModule = module {
    includes(dataModule, platformModule())
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        includes(config)
        modules(appModule)
    }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
// commonMain/kotlin/di/Koin.kt
import org.koin.core.annotation.*
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes
import org.koin.plugin.module.dsl.startKoin

@Module(includes = [PlatformModule::class])
@ComponentScan("com.example.app")
@Configuration
class AppModule

@KoinApplication
object KoinApp

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin<KoinApp> {
        includes(config)
    }
}
```

`@Configuration` modules are found by `@KoinApplication` at build time. `PlatformModule` is an `expect` module class, see [KMP Sharing Patterns](/docs/reference/koin-core/kmp-shared-modules).

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
// commonMain/kotlin/di/Koin.kt
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.*
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes
import org.koin.dsl.module

val dataModule = module {
    singleOf(::UserRepository)
    viewModelOf(::UserViewModel)
}

val appModule = module {
    includes(dataModule, platformModule())
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        includes(config)
        modules(appModule)
    }
}
```

</TabItem>
</Tabs>

`includes(config)` applies the platform configuration (Android context, logger, extra modules) to the same Koin start. `platformModule()` is an `expect` function, see [KMP Sharing Patterns](/docs/reference/koin-core/kmp-shared-modules).

## Platform entry points

### Android

Call `initKoin` from your `Application` class and pass the Android context:

```kotlin
// androidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MainApplication)
            androidLogger()
        }
    }
}
```

Android definitions can then use `Context` as a constructor parameter. See [Android Start](/docs/reference/koin-android/start).

### iOS

Add a function without parameters in `iosMain`, and call it from the Swift `App` initializer:

```kotlin
// iosMain/kotlin/di/KoinIos.kt
fun initKoinIos() = initKoin()
```

```swift
// iosApp/iOSApp.swift
import Shared

@main
struct iOSApp: App {
    init() {
        KoinIosKt.doInitKoinIos()
    }

    var body: some Scene {
        WindowGroup { ContentView() }
    }
}
```

Kotlin/Native naming rules explain the Swift call. Top-level functions of `KoinIos.kt` are exported in a class named `KoinIosKt`. A function whose name starts with `init` gets a `do` prefix, so `initKoinIos` becomes `doInitKoinIos`. The helper avoids passing a Kotlin lambda (`config`) from Swift.

Calling Koin from Swift code is covered in [KMP Platform Integration](/docs/reference/koin-mp/kmp).

### JVM desktop

```kotlin
// jvmMain
fun main() = application {
    initKoin {
        printLogger()
    }
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
```

### Web

```kotlin
// jsMain or wasmJsMain
fun main() {
    initKoin()
    // start your UI
}
```

## Compose Multiplatform

With Compose Multiplatform, you can start Koin from the root Composable with `KoinApplication` instead of calling `initKoin` on each platform. On Android, it sets the Android context for you:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
// commonMain
@Composable
fun App() {
    KoinApplication(configuration = koinConfiguration { modules(appModule) }) {
        MainScreen()
    }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
// commonMain
@Composable
fun App() {
    KoinApplication(configuration = koinConfiguration<KoinApp>()) {
        MainScreen()
    }
}
```

`koinConfiguration<T>()` comes from `org.koin.plugin.module.dsl`.

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
// commonMain
@Composable
fun App() {
    KoinApplication(configuration = koinConfiguration { modules(appModule) }) {
        MainScreen()
    }
}
```

</TabItem>
</Tabs>

Keep `initKoin` if code outside Compose needs Koin before the first screen (a background service, Swift code). Both options are compared in [Koin for Compose](/docs/reference/koin-compose/compose).

## Related

- [KMP Sharing Patterns](/docs/reference/koin-core/kmp-shared-modules): platform modules and `expect`/`actual` definitions.
- [KMP Platform Integration](/docs/reference/koin-mp/kmp): Android context in shared code, Swift interop, desktop and web.
- [Koin for Compose](/docs/reference/koin-compose/compose): `KoinApplication` and `koinInject()`.
- [ViewModel](/docs/reference/koin-core/viewmodel): ViewModels shared by Android and Compose Multiplatform.
- [Gradle Setup](/docs/setup/gradle): BOM, version catalog, artifact list.
