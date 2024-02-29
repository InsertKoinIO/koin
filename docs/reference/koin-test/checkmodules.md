---
title: Verifying your Koin configuration
---

:::note 
Koin allows you to verify your configuration modules, avoiding discovering dependency injection issues at runtime.
:::


### Koin Configuration check with Verify() - JVM Only [3.3]

Use the verify() extension function on a Koin Module. That's it! Under the hood, This will verify all constructor classes and crosscheck with the Koin configuration to know if there is a component declared for this dependency. In case of failure, the function will throw a MissingKoinDefinitionException.

```kotlin
val niaAppModule = module {
    includes(
        jankStatsKoinModule,
        dataKoinModule,
        syncWorkerKoinModule,
        topicKoinModule,
        authorKoinModule,
        interestsKoinModule,
        settingsKoinModule,
        bookMarksKoinModule,
        forYouKoinModule
    )
    viewModelOf(::MainActivityViewModel)
}
```


```kotlin
class NiaAppModuleCheck {

    @Test
    fun checkKoinModule() {

        // Verify Koin configuration
        niaAppModule.verify(
            // List types used in definitions but not declared directly (like parameters injection)
            extraTypes = listOf(...)
        )
    }
}
```


Launch the JUnit test and you're done! ✅


As you may see, we use the extra Types parameter to list types used in the Koin configuration but not declared directly. This is the case for SavedStateHandle and WorkerParameters types, that are used as injected parameters. The Context is declared by androidContext() function at start.


The verify() API is ultra light to run and doesn't require any kind of mock/stub to run on your configuration.


### Koin Dynamic Check - CheckModules()  


Invoke the `checkModules()` function within a simple JUnit test. This will launch your modules and try to run each possible definition for you. 


```kotlin
class CheckModulesTest : KoinTest {

    @Test
    fun verifyKoinApp() {
        
        koinApplication {
            modules(module1,module2)
            checkModules()
        }
    }
}
```

also possible to use `checkKoinModules`:

```kotlin
class CheckModulesTest : KoinTest {

    @Test
    fun verifyKoinApp() {
        
        checkKoinModules(listOf(module1,module2))
    }
}
```

#### CheckModule DSL

For any definition that is using injected parameters, properties or dynamic instances, the `checkModules` DSL allows to specify how to work with the following case:

* `withInstance(value)` - will add `value` instance to Koin graph (can be used in dependency or parameter)

* `withInstance<MyType>()` - will add a mocked instance of `MyType`. Use MockProviderRule. (can be used in dependency or parameter)

* `withParameter<Type>(qualifier){ qualifier -> value }` - will add `value` instance to be injected as parameter

* `withParameter<Type>(qualifier){ qualifier -> parametersOf(...) }` - will add `value` instance to be injected as parameter

* `withProperty(key,value)` - add property to Koin


#### Allow mocking with a Junit rule

To use mocks with `checkModules`, you need to provide a `MockProviderRule`

```kotlin
@get:Rule
val mockProvider = MockProviderRule.create { clazz ->
    // Mock with your framework here given clazz 
}
```

#### Verifying modules with dynamic behavior (3.1.3+)

To verify a dynamic behavior like following, let's use the CheckKoinModules DSL to provide the missing instance data to our test:

```kotlin
val myModule = module {
    factory { (id: String) -> FactoryPresenter(id) }
}
```

You can verify it with the following:

```kotlin
class CheckModulesTest : KoinTest {

    @Test
    fun verifyKoinApp() {
        
        koinApplication {
            modules(myModule)
            checkModules(){
                // value to add to Koin, used by definition
                withInstance("_my_id_value")
            }
        }
    }
}
```

This way, `FactoryPresenter` definition will be injected with `"_my_id_value"` define above.

You can also use mocked instances, to fill up your graph. You can notice that we need a `MockProviderRule` declaration to allow Koin mock any injected definition

```kotlin
val myModule1 = module {
    factory { (a : ComponentA) -> ComponentB(a) }
}
// or
val myModule2 = module {
    factory { ComponentB(get()) }
}
```

```kotlin
class CheckModulesTest : KoinTest {
    
    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        // Setup your nock framework
        Mockito.mock(clazz.java)
    }

    @Test
    fun verifyKoinApp() {
        
        koinApplication {
            modules(myModule1)
            checkModules(){
                // add a mock of ComponentA to Koin 
                withInstance<ComponentA>()
            }
        }
    }
}
```

#### Checking Modules for Android (3.1.3)

Here below is how you can test your graph for a typical Android app:

```kotlin
class CheckModulesTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Test
    fun `test DI modules`(){
        koinApplication {
            modules(allModules)
            checkModules(){
                withInstance<Context>()
                withInstance<Application>()
                withInstance<SavedStateHandle>()
                withInstance<WorkerParameters>()
            }
        }
    }
}
```

also possible to use `checkKoinModules`:

```kotlin
class CheckModulesTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Test
    fun `test DI modules`(){
        checkKoinModules(allModules) {
            withInstance<Context>()
            withInstance<Application>()
            withInstance<SavedStateHandle>()
            withInstance<WorkerParameters>()
        }
    }
}
```

#### Providing Default Values (3.1.4)

If you need, you can set a default value for all type in the checked modules. For example, we can override all injected string values:

Let's use the `withInstance()` function in `checkModules` block, to define a default value for all definitions:

```kotlin
@Test
fun `test DI modules`(){
    koinApplication {
        modules(allModules)
        checkModules(){
            withInstance("_ID_")
        }
    }
}
```

All injected definition that are using an injected `String` parameter, will receive `"_ID_"`:

```kotlin
module {
    single { (i: String) -> Simple.ComponentC(i) }
    factory { (id: String) -> FactoryPresenter(id) }
}
```

#### Providing ParametersOf values (3.1.4)

You can define a default value to be injected for one specific definition, with `withParameter` or `withParameters` functions:

```kotlin
@Test
fun `test DI modules`(){
    koinApplication {
        modules(allModules)
        checkModules(){
            withParameter<FactoryPresenter> { "_FactoryId_" }
            withParameters<FactoryPresenter> { parametersOf("_FactoryId_",...) }
        }
    }
}
```

#### Providing Scope Links

You can link scopes by using `withScopeLink` function in`checkModules` block to inject instances from another scope's definitions:

```kotlin
val myModule = module {
    scope(named("scope1")) {
        scoped { ComponentA() }
    }
    scope(named("scope2")) {
        scoped { ComponentB(get()) }
    }
}
```

```kotlin
@Test
fun `test DI modules`(){
    koinApplication {
        modules(myModule)
        checkModules(){
            withScopeLink(named("scope2"), named("scope1"))
        }
    }
}
```
