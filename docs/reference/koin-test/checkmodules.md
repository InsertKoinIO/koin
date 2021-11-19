---
title: Checking your modules or application graph
---

:::note 
Koin allows you to verify your configuration modules, avoiding to discover dependency injection issues at runtime.
:::

To verify your modules, you just need to the `checkKoinModules()` function within a simple JUnit test. This will launch your modules and try to run each possible definition for you. 



```kotlin
class CheckModulesTest : KoinTest {

    @Test
    fun verifyKoinApp() {
        
        checkKoinModules(module1,module2)
    }
}
```

#### CheckKoinModules DSL

For any definition that is using injected parameters, properties or dynamic instances, the `checkKoinModules` DSL allow to specify how to work with the following case:

* `withInstance(value)` - will add `value` instance to Koin graph (can be used in dependency or parameter)

* `withInstance<MyType>()` - will add a mocked instance of `MyType`. Use MockProviderRule. (can be used in depednency or parameter)

* `withParameter<Type>(qualifier){ qualifier -> value }` - will add `value` instance to be injected as parameter

* `withProperty(key,value)` - add property to Koin


#### Allow mocking with a Junit rule

To use mocks with `checkKoinModules`, you need to provide a `MockProviderRule`

```kotlin
@get:Rule
val mockProvider = MockProviderRule.create { clazz ->
    // Mock with your framework here given clazz
}
```

#### Verifying modules with dynamic behavior (since 3.1.3)

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
        
        checkKoinModules(myModule){
            // value to add to Koin, used by definition
            withInstance("_my_id_value")
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
        
        checkKoinModules(myModule1){
            // add a mock of ComponentA to Koin 
            withInstance<ComponentA>()
        }
    }
}
```

#### CheckKoinModules for Android

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
        checkKoinModules(allTestModules){
            withInstance<Context>()
            withInstance<Application>()
            withInstance<SavedStateHandle>()
            withInstance<WorkerParameters>()
        }
    }
}
```

#### Default values (deprecated)

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

#### Parameter creator (deprecated)

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