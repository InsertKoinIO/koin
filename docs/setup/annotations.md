---
title: Koin Annotations 1.0
---

Setup Koin Annotations for your project 

## Version

```groovy
koin_ksp_version= "1.0.3"
```

Everything is hosted on maven central:

```groovy
// Add Maven Central to your repositories if needed
repositories {
	mavenCentral()    
}
```

You can find all [Koin packages on maven central](https://search.maven.org/search?q=io.insert-koin).

:::info
 Koin 3.2 or newer is required
:::

## KSP Plugin

We need KSP Plugin to work (https://github.com/google/ksp). Just add the gradle plugin:

```groovy
ksp_version = "1.6.21-1.0.6"
```

```groovy
//at your project root

plugins {
    id "com.google.devtools.ksp" version "$ksp_version"
}
```

## Kotlin App Setup

Here below how you can configure a kotlin (even a ktor) app:

```groovy
// Use KSP Plugin
apply plugin: 'com.google.devtools.ksp'

// Use KSP Generated sources
sourceSets.main {
    java.srcDirs("build/generated/ksp/main/kotlin")
}

dependencies {
    // Koin
    compile "io.insert-koin:koin-core:$koin_version"
    compile "io.insert-koin:koin-annotations:$koin_ksp_version"
    ksp "io.insert-koin:koin-ksp-compiler:$koin_ksp_version"
}
```

## Android App Setup

Here below how you can configure an Android app:

```groovy
// Use KSP Plugin
apply plugin: 'com.google.devtools.ksp'

// Use KSP Generated sources
android {
    applicationVariants.all { variant ->
        variant.sourceSets.java.each {
            it.srcDirs += "build/generated/ksp/${variant.name}/kotlin"
        }
    }
}

dependencies {
    // Koin for Android
    implementation "io.insert-koin:koin-android:$koin_version"
    implementation "io.insert-koin:koin-annotations:$koin_ksp_version"
    ksp "io.insert-koin:koin-ksp-compiler:$koin_ksp_version"
}
```

If you use several KSP libraries (like Room), you can use this way of declaring generated sources:

```groovy
libraryVariants.all { variant ->
  variant.addJavaSourceFoldersToModel(file("build/generated/ksp/${variant.name}/kotlin"))
}
```

## Kotlin KMP Setup

Please follow KSP setup as described in official documentation: [KSP with Kotlin Multiplatform](https://kotlinlang.org/docs/ksp-multiplatform.html)

You can also check the [Hello Koin KMP](https://github.com/InsertKoinIO/hello-kmp/tree/annotations) project with basic setup for Koin Annotations.

