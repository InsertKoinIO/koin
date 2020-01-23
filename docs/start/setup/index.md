# Setup Koin for your project {docsify-ignore-all}

### Current Version

Here are the current versions of Koin:

```groovy
// Current stable version
koinVersion= "2.0.1"

// Latest unstable version
koinVersion= "2.1.0-alpha-9"
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
compile "org.koin:koin-core:$koinVersion"

// Koin Extended & experimental features
compile "org.koin:koin-core-ext:$koinVersion"

// Koin for Unit tests
testCompile "org.koin:koin-test:$koinVersion"
```

#### **Android**

```groovy
// Koin for Android
compile "org.koin:koin-android:$koinVersion"

// Koin Android Scope feature
compile "org.koin:koin-android-scope:$koinVersion"

// Koin Android ViewModel feature
compile "org.koin:koin-android-viewmodel:$koinVersion"
```

#### **AndroidX**

```groovy
// Koin AndroidX Scope feature
compile "org.koin:koin-androidx-scope:$koinVersion"

// Koin AndroidX ViewModel feature
compile "org.koin:koin-androidx-viewmodel:$koinVersion"

// Koin AndroidX Fragment Factory (unstable version)
compile "org.koin:koin-androidx-fragment:$koinVersion"
```

#### **Ktor**

```groovy
// Koin for Ktor Kotlin
compile "org.koin:koin-ktor:$koinVersion"
```

<!-- tabs:end -->