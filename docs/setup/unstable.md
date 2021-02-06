---
title: Unstable Version
---

 Setup Koin for your project 

### Current Version

Here are the current versions of Koin:

```groovy
// Current stable version
koin_version= "3.0.1-alpha-3"
```

### Gradle dependencies

Add the following Gradle dependencies to add Koin to your project:

```groovy
// Add Jcenter to your repositories if needed
repositories {
    jcenter()
}
```

#### **Kotlin**

```groovy
// Koin for Kotlin Multiplatform
implementation "org.koin:koin-core:$koin_version"

// Koin Test for Kotlin Multiplatform
testImplementation "org.koin:koin-test:$koin_version"
// Koin for JUnit 4
testImplementation "org.koin:koin-test-junit4:$koin_version"
// Koin for JUnit 5
testImplementation "org.koin:koin-test-junit5:$koin_version"

// Koin Extended & experimental features (JVM)
implementation "org.koin:koin-core-ext:$koin_version"
```

#### **Android**

```groovy
// Koin main features for Android (Scope,ViewModel ...)
implementation "org.koin:koin-android:$koin_version"
// Koin Android - experimental builder extensions
implementation "org.koin:koin-android-ext:$koin_version"
// Koin for Jetpack WorkManager
implementation "org.koin:koin-androidx-workmanager:$koin_version"
// Koin for Jetpack Compose (unstable version)
implementation "org.koin:koin-androidx-compose:$koin_version"
```


#### **Ktor**

```groovy
// Koin for Ktor 
implementation "org.koin:koin-ktor:$koin_version"
// SLF4J Logger
implementation "org.koin:koin-logger-slf4j:$koin_version"
```