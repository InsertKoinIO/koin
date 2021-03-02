---
title: Unstable Version
---

 Setup Koin for your project 

### Current Version

Here are the current versions of Koin:

```groovy
// Current stable version
koin_version= "3.0.1-alpha-6"
```

### Gradle dependencies

Add the following Gradle dependencies to add Koin to your project:

```groovy
// Add Jcenter to your repositories if needed
repositories {
    mavenCentral()
}
```

#### **Kotlin**

```groovy
// Koin for Kotlin Multiplatform
implementation "io.insert-koin:koin-core:$koin_version"

// Koin Test for Kotlin Multiplatform
testImplementation "io.insert-koin:koin-test:$koin_version"
// Koin for JUnit 4
testImplementation "io.insert-koin:koin-test-junit4:$koin_version"
// Koin for JUnit 5
testImplementation "io.insert-koin:koin-test-junit5:$koin_version"

// Koin Extended & experimental features (JVM)
implementation "io.insert-koin:koin-core-ext:$koin_version"
```

#### **Android**

```groovy
// Koin main features for Android (Scope,ViewModel ...)
implementation "io.insert-koin:koin-android:$koin_version"
// Koin Android - experimental builder extensions
implementation "io.insert-koin:koin-android-ext:$koin_version"
// Koin for Jetpack WorkManager
implementation "io.insert-koin:koin-androidx-workmanager:$koin_version"
// Koin for Jetpack Compose (unstable version)
implementation "io.insert-koin:koin-androidx-compose:$koin_version"
```


#### **Ktor**

```groovy
// Koin for Ktor 
implementation "io.insert-koin:koin-ktor:$koin_version"
// SLF4J Logger
implementation "io.insert-koin:koin-logger-slf4j:$koin_version"
```
