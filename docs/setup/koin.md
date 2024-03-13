---
title: Koin
---

All you need to setting up Koin in your project

## Current Versions

You can find all Koin packages on [maven central](https://search.maven.org/search?q=io.insert-koin).

Here are the currently available versions:

| Project                          |                                                                                            Version                                                                                             |
|----------------------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| koin-bom                         |                         [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-bom)](https://mvnrepository.com/artifact/io.insert-koin/koin-bom)                         |
| koin-core                        |                        [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-core)](https://mvnrepository.com/artifact/io.insert-koin/koin-core)                        |
| koin-core-coroutines             |             [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-core-coroutines)](https://mvnrepository.com/artifact/io.insert-koin/koin-core-coroutines)             |
| koin-test                        |                        [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-test)](https://mvnrepository.com/artifact/io.insert-koin/koin-test)                        |
| koin-android                     |                     [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-android)](https://mvnrepository.com/artifact/io.insert-koin/koin-android)                     |
| koin-android-test                |                [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-android-test)](https://mvnrepository.com/artifact/io.insert-koin/koin-android-test)                |
| koin-android-compat              |              [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-android-compat)](https://mvnrepository.com/artifact/io.insert-koin/koin-android-compat)              |
| koin-androidx-navigation         |         [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-androidx-navigation)](https://mvnrepository.com/artifact/io.insert-koin/koin-androidx-navigation)         |
| koin-androidx-workmanager        |        [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-androidx-workmanager)](https://mvnrepository.com/artifact/io.insert-koin/koin-androidx-workmanager)        |
| koin-compose                     |                     [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-compose)](https://mvnrepository.com/artifact/io.insert-koin/koin-compose)                     |
| koin-androidx-compose            |            [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-androidx-compose)](https://mvnrepository.com/artifact/io.insert-koin/koin-androidx-compose)            |
| koin-androidx-compose-navigation | [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-androidx-compose-navigation)](https://mvnrepository.com/artifact/io.insert-koin/koin-androidx-compose-navigation) |
| koin-ktor                        |                        [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-ktor)](https://mvnrepository.com/artifact/io.insert-koin/koin-ktor)                        |
| koin-logger-slf4j                |                [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-logger-slf4j)](https://mvnrepository.com/artifact/io.insert-koin/koin-logger-slf4j)                |

## Gradle Setup

### Kotlin

Starting from 3.5.0 you can use BOM-version to manage all Koin library versions. When using the BOM in your app, you don't need to add any version to the Koin library dependencies themselves. When you update the BOM version, all the libraries that you're using are automatically updated to their new versions.

Add `koin-bom` BOM and `koin-core` dependency to your application: 
```kotlin
implementation(platform("io.insert-koin:koin-bom:$koin_version"))
implementation("io.insert-koin:koin-core")
```
If you are using version catalogs:
```toml
[versions]
koin-bom = "x.x.x"
...

[libraries]
koin-bom = { module = "io.insert-koin:koin-bom", version.ref = "koin-bom" }
koin-core = { module = "io.insert-koin:koin-core" }
...
```
```kotlin
dependencies {
    implementation(libs.koin.bom)
    implementation(libs.koin.core)
}
```

Or use an old way of specifying the exact dependency version for Koin:
```kotlin
dependencies {
    implementation("io.insert-koin:koin-core:$koin_version")
}
```

You are now ready to start Koin:

```kotlin
fun main() {
    startKoin {
        modules(...)
    }
}
```

If you need testing capacity:

```groovy
dependencies {
    // Koin Test features
    testImplementation("io.insert-koin:koin-test:$koin_version")
    // Koin for JUnit 4
    testImplementation("io.insert-koin:koin-test-junit4:$koin_version")
    // Koin for JUnit 5
    testImplementation("io.insert-koin:koin-test-junit5:$koin_version")
}
```

:::info
From now you can continue on Koin Tutorials to learn about using Koin: [Kotlin App Tutorial](../quickstart/kotlin)
:::

### **Android**

Add `koin-android` dependency to your Android application:

```groovy
dependencies {
    implementation("io.insert-koin:koin-android:$koin_android_version")
}
```

You are now ready to start Koin in your `Application` class:

```kotlin
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            modules(appModule)
        }
    }
}
```

If you need extra features, add the following needed package:

```groovy
dependencies {
    // Java Compatibility
    implementation("io.insert-koin:koin-android-compat:$koin_android_version")
    // Jetpack WorkManager
    implementation("io.insert-koin:koin-androidx-workmanager:$koin_android_version")
    // Navigation Graph
    implementation("io.insert-koin:koin-androidx-navigation:$koin_android_version")
}
```

:::info
From now you can continue on Koin Tutorials to learn about using Koin: [Android App Tutorial](../quickstart/android-viewmodel)
:::

### **Android Jetpack Compose**

```groovy
dependencies {
    implementation("io.insert-koin:koin-androidx-compose:$koin_android_compose_version")
}
```

You are now ready to start Koin in your `Application` class:

```kotlin
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            modules(appModule)
        }
    }
}
```

:::info
From now you can continue on Koin Tutorials to learn about using Koin: [Android Compose App Tutorial](../quickstart/android-compose)
:::


### **Kotlin Multiplatform**

Add `koin-core` dependency to your multiplatform application, for shared Kotlin part:

```groovy
dependencies {
    implementation("io.insert-koin:koin-core:$koin_version")
}
```

:::info
From now you can continue on Koin Tutorials to learn about using Koin: [Kotlin Multiplatform App Tutorial](../quickstart/kmm)
:::

### **Ktor**

Add `koin-ktor` dependency to your Ktor application:

```groovy
dependencies {
    // Koin for Ktor 
    implementation("io.insert-koin:koin-ktor:$koin_ktor")
    // SLF4J Logger
    implementation("io.insert-koin:koin-logger-slf4j:$koin_ktor")
}
```

You are now ready to install Koin feature into your Ktor application:

```kotlin
fun Application.main() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}
```

:::info
From now you can continue on Koin Tutorials to learn about using Koin: [Ktor App Tutorial](../quickstart/ktor)
:::


### **Koin BOM**
The Koin Bill of Materials (BOM) lets you manage all of your Koin library versions by specifying only the BOM’s version. The BOM itself has links to the stable versions of the different Koin libraries, in such a way that they work well together. When using the BOM in your app, you don't need to add any version to the Koin library dependencies themselves. When you update the BOM version, all the libraries that you're using are automatically updated to their new versions.

```groovy
dependencies {
    // Declare koin-bom version
    implementation platform("io.insert-koin:koin-bom:$koin_bom")
    
    // Declare the koin dependencies that you need
    implementation("io.insert-koin:koin-android")
    implementation("io.insert-koin:koin-core-coroutines")
    implementation("io.insert-koin:koin-androidx-workmanager")
    
    // If you need specify some version it's just point to desired version
    implementation("io.insert-koin:koin-androidx-navigation:1.2.3-alpha03")
    
    // Works with test libraries too!
    testImplementation("io.insert-koin:koin-test-junit4")
    testImplementation("io.insert-koin:koin-android-test")
}
```
