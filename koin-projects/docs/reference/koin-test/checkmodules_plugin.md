
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

### Checking your modules

You need to add a JUnit category to let us run your checkModules tests: `@Category(CheckModuleTest::class)`

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

#### Verifying graph with injected parameters [since 2.2.0]

For any definition that is using injected parameters, the `checkModules` function provide default values or even mock object to help verify your definition:

In the module below, Koin will verify your graph by passing a mock of `a : Simple.ComponentA` and a default value for `id : String`. 

```kotlin
module {
    single { (a: Simple.ComponentA) -> Simple.ComponentB(a) }
    factory { (id: String) -> FactoryPresenter(id) }
}
```

#### Default values [since 2.2.0]

If you need, you can set a default value for all type in the checked modules. For example, We can override all injected string values:

Let's use the `defaultValues()` function, to define a default value for all definitions:

```kotlin
@Category(CheckModuleTest::class)
class CheckModulesTest : KoinTest {

    @Test
    fun checkAllModules() = checkModules(
        parameters = {
            defaultValues<String>("_ID_")
        }   
    ){
        modules(myModules)
    }
}
```

All injected definition that are using a injected `String` parameter, will receive `"_ID_"`:

```kotlin
module {
    single { (i: String) -> Simple.ComponentC(i) }
    factory { (id: String) -> FactoryPresenter(id) }
}
```

#### Parameter creator

You can define default value to be injected for one specific definition, with `create` function:

```kotlin
@Category(CheckModuleTest::class)
class CheckModulesTest : KoinTest {

    @Test
    fun checkAllModules() = checkModules(
        parameters = {
            create<FactoryPresenter> { parametersOf("_FactoryId_") }
        }   
    ){
        modules(myModules)
    }
}
```

### Checking injected parameters definitions

### Verify your modules (Kotlin project) - checkModules task

Once done, you can verify your modules:

```
./gradlew checkModules
```

You can also verify your modules in a continuous way:

```
./gradlew checkModules --continuous
```

### Verify your modules (Android project) - checkAndroidModules task

Once done, you can verify your modules:

```
./gradlew checkAndroidModules
```

You can also verify your modules in a continuous way:

```
./gradlew checkAndroidModules --continuous
```