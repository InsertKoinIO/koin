---
title: Kotlin Multiplatform Dependency Injection
---

## Source Project

:::info
 You can find the Kotlin Multiplatform project here: https://github.com/InsertKoinIO/hello-kmp
:::

## Gradle Dependencies

Koin is a pure Kotlin library and can be used in your shared Kotlin project. Just add the core dependency:

Add `koin-core` in common project, declare your dependency: [https://github.com/InsertKoinIO/hello-kmp/tree/main/buildSrc](https://github.com/InsertKoinIO/hello-kmp/blob/main/buildSrc/src/main/java/Dependencies.kt)


```kotlin
// Dependencies.kt

object Versions {
    const val koin = "3.2.0"
}

object Deps {

    object Koin {
        const val core = "io.insert-koin:koin-core:${Versions.koin}"
        const val test = "io.insert-koin:koin-test:${Versions.koin}"
        const val android = "io.insert-koin:koin-android:${Versions.koin}"
    }

}
```

## Shared Koin Module

Platform specific components can be declared here, and be used later in Android or iOS (declared directly with actual classes or even actual module)

You can find the shared module sources here: https://github.com/InsertKoinIO/hello-kmp/tree/main/shared

```kotlin
// platform Module
val platformModule = module {
    singleOf(::Platform)
}

// KMP Class Definition
expect class Platform() {
    val name: String
}

// iOS
actual class Platform actual constructor() {
    actual val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

// Android
actual class Platform actual constructor() {
    actual val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}
```

Koin modules need to be gathered via a function:

```kotlin
// Common App Definitions
fun appModule() = listOf(commonModule, platformModule)
```

## Android App

You can keep using `koin-android` features and reuse the common modules/classes.

The code for the Android app can be found here: https://github.com/InsertKoinIO/hello-kmp/tree/main/androidApp

## iOS App

The code for the iOS App can be found here: https://github.com/InsertKoinIO/hello-kmp/tree/main/iosApp

### Calling Koin

Let’s prepare a wrapper to our Koin function (in our shared code):

```kotlin
// Helper.kt

fun initKoin(){
    startKoin {
        modules(appModule())
    }
}
```

We can init it in our Main app entry:

```kotlin
@main
struct iOSApp: App {
    
        // KMM - Koin Call
    init() {
        HelperKt.doInitKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

### Injected Classes

Let’s call a Kotlin class instance from swift.

Our Kotlin component:

```kotlin
// Injection Boostrap Helper
class GreetingHelper : KoinComponent {
    private val greeting : Greeting by inject()
    fun greet() : String = greeting.greeting()
}
```

In our swift app:

```kotlin
struct ContentView: View {
        // Create helper instance
    let greet = GreetingHelper().greet()

    var body: some View {
        Text(greet)
    }
}
```

### New Native Memory Management

Activate experiemental with root [gradle.properties](http://gradle.properties) properties:
