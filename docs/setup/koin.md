---
title: Koin 3.3
---

 Setup Koin for your project 

## Version

You can find all [Koin packages on maven central](https://search.maven.org/search?q=io.insert-koin).

## Gradle dependencies

Add the following Gradle dependencies to add Koin to your project:

```groovy
// Add Maven Central to your repositories if needed
repositories {
    mavenCentral()
}
```

### **Core**

```groovy
koin_version= "3.3.0"
```

```groovy
// Koin Core features
implementation "io.insert-koin:koin-core:$koin_version"
// Koin Test features
testImplementation "io.insert-koin:koin-test:$koin_version"

// Koin for JUnit 4
testImplementation "io.insert-koin:koin-test-junit4:$koin_version"
// Koin for JUnit 5
testImplementation "io.insert-koin:koin-test-junit5:$koin_version"
```

### **Android**

```groovy
koin_android_version= "3.3.1"
```

```groovy
// Koin main features for Android
implementation "io.insert-koin:koin-android:$koin_android_version"
// Java Compatibility
implementation "io.insert-koin:koin-android-compat:$koin_android_version"
// Jetpack WorkManager
implementation "io.insert-koin:koin-androidx-workmanager:$koin_android_version"
// Navigation Graph
implementation "io.insert-koin:koin-androidx-navigation:$koin_android_version"
```

### **Android Jetpack Compose**

```groovy
koin_android_compose_version= "3.4.0"
```

```groovy
// Jetpack Compose
implementation "io.insert-koin:koin-androidx-compose:$koin_android_compose_version"
```


### **Ktor**

```groovy
koin_ktor= "3.2.2"
```

```groovy
// Koin for Ktor 
implementation "io.insert-koin:koin-ktor:$koin_ktor"
// SLF4J Logger
implementation "io.insert-koin:koin-logger-slf4j:$koin_ktor"
```
