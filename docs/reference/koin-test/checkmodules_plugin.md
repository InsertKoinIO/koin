
## Checking your modules with Koin Gradle Plugin

The Koin gradle plugin allow you to run `checkModules` task to run your modules verification.

### Gradle plugin

First, setup the Gradle plugin: 

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

### The JUnit CheckModules test

You need to add a JUnit catgeory to let us run your checkModules tests: `@Category(CheckModuleTest::class)`

Each of the module test will use the `checkModules` API to check the module:

```kotlin
@Category(CheckModuleTest::class)
class CheckModulesTest : KoinTest {

    @Test
    fun checkAllModules() = checkModules {
        modules(appModule)
    }
}
```

### Verify your modules

Once done, you can verify your modules:

```
./gradlew checkModules
```

You can also verify your modules in a continuous way:

```
./gradlew checkModules --continuous
```