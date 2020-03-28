
## Making your test a KoinComponent with KoinTest

By tagging your class `KoinTest`, your class become a `KoinComponent` and bring you:

* `by inject()` & `get()` - function to retrieve yoru instances from Koin
* `checkModules` - help you check your configuration
* `declareMock` & `declare` - to declare a mock or a new definition in the current context

```kotlin
class ComponentA
class ComponentB(val a: ComponentA)

class MyTest : KoinTest {

    // Lazy inject property
    val componentB : ComponentB by inject()

    @Test
    fun `should inject my components`() {
        startKoin{
            modules(
                module {
                    single { ComponentA() }
                    single { ComponentB(get()) }
                })
        }

        // directly request an instance
        val componentA = get<ComponentA>()

        assertNotNull(a)
        assertEquals(componentA, componentB.a)
    }
```

?> Don't hesitate to overload Koin modules configuration to help you partly build your app.

## JUnit Rules

### Create a Koin context for your test

You can easily create and hold a Koin context for each of your test with the following rule:

```kotlin
@get:Rule
val koinTestRule = KoinTestRule.create {
    // Your KoinApplication instance here
    modules(myModule)
}
```

### Specify your Mock Provider

To let you use the `declareMock` API, you need to specify a rule to let Koin know how you build your Mock instance. This let you choose the right
mocking framework for your need. Below is a Mockito example: 

```kotlin
@get:Rule
val mockProvider = MockProviderRule.create { clazz ->
    // Your way to build a Mock here
    Mockito.mock(clazz.java)
}
```

!> koin-test project is not tied anymroe to mockito

## Mocking out of the box

Instead of making a new module each time you need a mock, you can declare a mock on the fly with `declareMock`:

```kotlin
class ComponentA
class ComponentB(val a: ComponentA)

class MyTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(
            module {
                single { ComponentA() }
                single { ComponentB(get()) }
            })
    }

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }
    
    @Test
    fun `should inject my components`() {
    
    }
        // Replace current definition by a Mock
        val mock = declareMock<ComponentA>()

        // retrieve mock, same as variable above 
        assertNotNull(get<ComponentA>())

        // is built with mocked ComponentA
        assertNotNull(get<ComponentB>())
    }
```

?> declareMock can specify if you want a single or factory, and if you wan to have it in a module path.

## Declaring a component on the fly

When a mock is not enough and don't want to create a module just for this, you can use `declare`:

```kotlin
    @Test
    fun `successful declare an expression mock`() {
        startKoin { }

        declare {
            factory { ComponentA("Test Params") }
        }

        Assert.assertNotEquals(get<ComponentA>(), get<ComponentA>())
    }
```

## Checking your Koin modules

Koin offers a way to test if you Koin modules are good: `checkModules` - walk through your definition tree and check if each definition is bound

```kotlin
    @Test
    fun `check MVP hierarchy`() {
        checkModules {
            modules(myModule1, myModule2 ...)
        } 
    }
```

## Starting & stopping Koin for your tests

Take attention to stop your koin instance (if you use `startKoin` in your tests) between every test. Else be sure to use `koinApplication`, for local koin instances or `stopKoin()` to stop the current global instance.


