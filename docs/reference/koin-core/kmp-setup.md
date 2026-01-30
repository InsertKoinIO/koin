---
title: Kotlin Multiplatform Setup
---

# Kotlin Multiplatform Setup

Koin provides first-class support for Kotlin Multiplatform (KMP) projects. This guide covers setup and configuration.

:::info
For definition types (Single, Factory, ViewModel) and the three declaration approaches (Compiler Plugin DSL, Annotations, Classic DSL), see [Definitions](/docs/reference/koin-core/definitions).
:::

## Supported Platforms

| Platform | Status |
|----------|--------|
| Android | ✅ Full support |
| iOS (arm64, x64, simulatorArm64) | ✅ Full support |
| JVM | ✅ Full support |
| JS | ✅ Full support |
| Wasm | ✅ Full support |
| macOS | ✅ Full support |
| Linux | ✅ Full support |
| Windows | ✅ Full support |

## Dependencies Setup

### shared/build.gradle.kts

```kotlin
plugins {
    kotlin("multiplatform")
    id("io.insert-koin.compiler.plugin")  // Optional: for Compiler Plugin
}

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    jvm()
    js(IR) { browser() }

    sourceSets {
        commonMain.dependencies {
            implementation(platform("io.insert-koin:koin-bom:4.2.0"))
            implementation("io.insert-koin:koin-core")
        }

        commonTest.dependencies {
            implementation("io.insert-koin:koin-test")
        }

        androidMain.dependencies {
            implementation("io.insert-koin:koin-android")
        }
    }
}
```

### With Compose Multiplatform

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(platform("io.insert-koin:koin-bom:4.2.0"))
            implementation("io.insert-koin:koin-core")
            implementation("io.insert-koin:koin-compose")
            implementation("io.insert-koin:koin-compose-viewmodel")
        }
    }
}
```

## Project Structure

```
project/
├── shared/
│   ├── src/
│   │   ├── commonMain/
│   │   │   └── kotlin/
│   │   │       ├── di/
│   │   │       │   └── KoinModules.kt
│   │   │       └── domain/
│   │   │           └── UserRepository.kt
│   │   ├── androidMain/
│   │   │   └── kotlin/
│   │   │       └── di/
│   │   │           └── PlatformModule.android.kt
│   │   └── iosMain/
│   │       └── kotlin/
│   │           └── di/
│   │               └── PlatformModule.ios.kt
│   └── build.gradle.kts
├── androidApp/
│   └── src/main/kotlin/
│       └── MainApplication.kt
└── iosApp/
    └── iOSApp.swift
```

## Common Module Definition

### commonMain/kotlin/di/KoinModules.kt

```kotlin
import org.koin.dsl.module

// Shared definitions (Compiler Plugin DSL)
val sharedModule = module {
    single<UserRepository>()
    single<ApiClient>()
    factory<GetUserUseCase>()
}

// Platform-specific modules (defined per platform)
expect val platformModule: Module
```

:::note
Compiler Plugin DSL (`single<Type>()`) is recommended for shared modules. It requires the compiler plugin but provides the cleanest syntax with no per-platform KSP configuration.
:::

## Platform-Specific Modules

Platform modules can use any approach. Classic DSL with lambdas is shown here for cases requiring custom construction logic.

### androidMain/kotlin/di/PlatformModule.android.kt

```kotlin
import org.koin.dsl.module

actual val platformModule = module {
    // Classic DSL with lambda for custom construction
    single<PlatformHelper> { AndroidPlatformHelper(get()) }
    single<DatabaseDriver> { AndroidDatabaseDriver(get()) }
}
```

### iosMain/kotlin/di/PlatformModule.ios.kt

```kotlin
import org.koin.dsl.module

actual val platformModule = module {
    // Or use Compiler Plugin DSL / Annotations if no custom logic needed
    single<IosPlatformHelper>() bind PlatformHelper::class
    single<IosDatabaseDriver>() bind DatabaseDriver::class
}
```

## Shared Initialization

### commonMain/kotlin/di/KoinInit.kt

```kotlin
import org.koin.core.context.startKoin
import org.koin.core.KoinApplication

fun initKoin(config: KoinAppDeclaration? = null): KoinApplication {
    return startKoin {
        includes(config)
        modules(
            sharedModule,
            platformModule
        )
    }
}
```

## Platform Entry Points

### Android

```kotlin
// androidApp/src/main/kotlin/MainApplication.kt
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

### iOS

```kotlin
// shared/src/iosMain/kotlin/di/KoinInitIos.kt
fun initKoinIos() {
    initKoin()
}
```

```swift
// iosApp/iOSApp.swift
import shared

@main
struct iOSApp: App {
    init() {
        KoinInitIosKt.initKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

### JVM

```kotlin
fun main() {
    initKoin {
        printLogger()
    }

    val repository: UserRepository = get()
}
```

## DSL Approaches in KMP

| Approach | When to Use |
|----------|-------------|
| **Compiler Plugin DSL** | Default choice - works everywhere, cleanest syntax |
| **Annotations** | Default choice - works everywhere, no module code needed |
| **Classic DSL with lambda** | Builder patterns, custom factory logic, mocks |

:::info
**Compiler Plugin DSL** and **Annotations** work everywhere. Use **Classic DSL with lambda** only when you need custom construction logic. See [Compiler Plugin Setup](/docs/setup/compiler-plugin) for details.
:::

## Best Practices

1. **Put shared code in commonMain** - Business logic, repositories, use cases
2. **Use expect/actual for platform specifics** - File system, device APIs, platform libraries
3. **Initialize Koin per platform** - Each platform has its entry point
4. **Keep platform modules minimal** - Only what's truly platform-specific

## Next Steps

- **[Sharing Patterns](/docs/reference/koin-core/kmp-shared-modules)** - Module organization, expect/actual patterns
- **[ViewModel](/docs/reference/koin-core/viewmodel)** - Multiplatform ViewModel
- **[Advanced Patterns](/docs/reference/koin-mp/kmp)** - Architecture patterns, testing, platform integration
- **[Testing](/docs/reference/koin-test/testing)** - Testing KMP projects
