---
title: Checking your definitions
---

To check your modules, you just need to run it with the `checkModules()` function via a JUnit test.

Each of the module test will use the `checkModules` API to check the module:

```kotlin
class CheckModulesTest : KoinTest {
    
    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Test
    fun checkAllModules() = checkModules {
        modules(appModule)
        // other KoinApplication attributes if needed
    }
}
```

> You can notice that we need a `MockProviderRule` declaration to allow Koin mock any injected definition

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
@Test
fun checkAllModules() = checkModules(
    parameters = {
        defaultValues<String>("_ID_")
    }   
){
    modules(myModules)
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
@Test
fun checkAllModules() = checkModules(
    parameters = {
        create<FactoryPresenter> { parametersOf("_FactoryId_") }
        // or
        create(FactoryPresenter::class) { parametersOf("_FactoryId_") }
    }   
){
    modules(myModules)
}
```