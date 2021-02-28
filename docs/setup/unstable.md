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



## Maven

Pick one of your Koin dependency:

#### Maven Repository

```xml
<repositories>
    <repository>
        <id>jcenter</id>
        <name>jcenter</name>
        <url>https://jcenter.bintray.com</url>
    </repository>
</repositories>
```

### Set version

```
<properties>
    <koin.version>3.0.1-alpha-3</koin.version>
</properties>
```

#### Kotlin

```xml
<dependencies>
    <!-- Koin for Kotlin -->
    <dependency>
        <groupId>org.koin</groupId>
        <artifactId>koin-core</artifactId>
        <version>${koin.version}</version>
    </dependency>
    
    <!-- Koin extended & experimental features (JVM) -->
    <dependency>
        <groupId>org.koin</groupId>
        <artifactId>koin-core-ext</artifactId>
        <version>${koin.version}</version>
    </dependency>
    
    <!-- Koin for Unit tests -->
    <dependency>
        <groupId>org.koin</groupId>
        <artifactId>koin-test</artifactId>
        <version>${koin.version}</version>
        <scope>test</scope>
    </dependency>

    <!-- Koin for JUnit 4 -->
    <dependency>
        <groupId>org.koin</groupId>
        <artifactId>koin-test-junit4</artifactId>
        <version>${koin.version}</version>
        <scope>test</scope>
    </dependency>

    <!-- Koin for JUnit 5 -->
    <dependency>
        <groupId>org.koin</groupId>
        <artifactId>koin-test-junit5</artifactId>
        <version>${koin.version}</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

#### Android

```xml
<dependencies>
    <!-- Koin for Android -->
    <dependency>
        <groupId>org.koin</groupId>
        <artifactId>koin-android</artifactId>
        <version>${koin.version}</version>
    </dependency>

    <!-- Koin Android Experimental features -->
     <dependency>
        <groupId>org.koin</groupId>
        <artifactId>koin-android-ext</artifactId>
        <version>${koin.version}</version>
    </dependency>

    <!-- Koin AndroidX WorkManager -->
    <dependency>
        <groupId>org.koin</groupId>
        <artifactId>koin-androidx-workmanager</artifactId>
        <version>${koin.version}</version>
    </dependency>

    <!-- Koin AndroidX Jetpack Compose -->
    <dependency>
        <groupId>org.koin</groupId>
        <artifactId>koin-androidx-compose</artifactId>
        <version>${koin.version}</version>
    </dependency>
</dependencies>
```

#### Ktor

```xml
<dependencies>
    <!-- Koin for Ktor Kotlin -->
    <dependency>
        <groupId>org.koin</groupId>
        <artifactId>koin-ktor</artifactId>
        <version>${koin.version}</version>
    </dependency>

    <!-- SLF4J Logger -->
    <dependency>
        <groupId>org.koin</groupId>
        <artifactId>    <!-- Koin for Ktor Kotlin--></artifactId>
        <version>${koin.version}</version>
    </dependency>
</dependencies>
```
