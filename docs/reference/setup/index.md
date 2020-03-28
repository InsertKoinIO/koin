# Setup Koin for your project {docsify-ignore-all}

### Current Version

Here are the current versions of Koin:

```groovy
// Current stable version
koin_version= "2.1.5"
```

### Gradle dependencies

Add the following Gradle dependencies to add Koin to your project:

?> Koin packages are published on JCenter

```groovy
// Add Jcenter to your repositories if needed
repositories {
    jcenter()
}
```

<!-- tabs:start -->

#### **Kotlin**

```groovy
// Koin for Kotlin
implementation "org.koin:koin-core:$koin_version"

// Koin Extended & experimental features
implementation "org.koin:koin-core-ext:$koin_version"

// Koin for Unit tests
testImplementation "org.koin:koin-test:$koin_version"
```

#### **Gradle Plugin**

```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath "org.koin:koin-gradle-plugin:$koin_version"
    }
}

apply plugin: 'koin'
```

#### **Android**

```groovy
// Koin for Android
implementation "org.koin:koin-android:$koin_version"

// Koin Android Scope feature
implementation "org.koin:koin-android-scope:$koin_version"

// Koin Android ViewModel feature
implementation "org.koin:koin-android-viewmodel:$koin_version"
```

#### **AndroidX**

```groovy
// Koin AndroidX Scope feature
implementation "org.koin:koin-androidx-scope:$koin_version"

// Koin AndroidX ViewModel feature
implementation "org.koin:koin-androidx-viewmodel:$koin_version"

// Koin AndroidX Fragment Factory (unstable version)
implementation "org.koin:koin-androidx-fragment:$koin_version"
```

#### **Ktor**

```groovy
// Koin for Ktor Kotlin
implementation "org.koin:koin-ktor:$koin_version"
```

<!-- tabs:end -->