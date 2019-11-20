# Setup Koin for your project {docsify-ignore-all}

### Current Version

Here are the current versions of Koin:

```groovy
// Current stable version
koin_version= "2.0.1"

// Latest unstable version
koin_version= "2.1.0-alpha-3"
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
compile "org.koin:koin-core:$koin_version"

// Koin Extended & experimental features
compile "org.koin:koin-core-ext:$koin_version"

// Koin for Unit tests
testCompile "org.koin:koin-test:$koin_version"
```

#### **Android**

```groovy
// Koin for Android
compile "org.koin:koin-android:$koin_version"

// Koin Android Scope feature
compile "org.koin:koin-android-scope:$koin_version"

// Koin Android ViewModel feature
compile "org.koin:koin-android-viewmodel:$koin_version"
```

#### **AndroidX**

```groovy
// AndroidX (based on koin-android)
// Koin AndroidX Scope feature
compile "org.koin:koin-androidx-scope:$koin_version"

// Koin AndroidX ViewModel feature
compile "org.koin:koin-androidx-viewmodel:$koin_version"


// Koin AndroidX Fragment Factory (unstable version)
compile "org.koin:koin-androidx-fragment:$koin_version"
```

#### **Ktor**

```groovy
// Koin for Ktor Kotlin
compile "org.koin:koin-ktor:$koin_version"
```

<!-- tabs:end -->