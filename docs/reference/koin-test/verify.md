---
title: Verifying your Koin configuration
---

Koin allows you to verify your configuration modules, avoiding discovering dependency injection issues at runtime.

:::info Future: Compile-Time Safety
Both `verify()` and `checkModules()` APIs will be replaced by **native compile-time safety** in the Koin Compiler Plugin. This will validate your entire configuration at build time, catching errors before runtime.

See [Koin Compiler Plugin](/docs/intro/koin-compiler-plugin) for more information.
:::

## Verify API - JVM Only [3.3+]

Use the `verify()` extension function on a Koin Module. Under the hood, this will verify all constructor classes and crosscheck with the Koin configuration to know if there is a component declared for this dependency. In case of failure, the function will throw a `MissingKoinDefinitionException`.

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
    viewModel<MainActivityViewModel>()
}
```

```kotlin
class NiaAppModuleCheck {

    @Test
    fun checkKoinModule() {
        // Verify Koin configuration
        niaAppModule.verify()
    }
}
```

Launch the JUnit test and you're done!

The `verify()` API is ultra light to run and doesn't require any kind of mock/stub to run on your configuration.

### Verifying with Injected Parameters [4.0+]

When you have a configuration that implies injected objects with `parametersOf`, the verification will fail because there is no definition of the parameter's type in your configuration.
However you can define a parameter type, to be injected with given definition `definition<Type>(Class1::class, Class2::class ...)`.

```kotlin
class ModuleCheck {

    // given a definition with an injected definition
    val module = module {
        single { (a: Simple.ComponentA) -> Simple.ComponentB(a) }
    }

    @Test
    fun checkKoinModule() {

        // Verify and declare Injected Parameters
        module.verify(
            injections = injectedParameters(
                definition<Simple.ComponentB>(Simple.ComponentA::class)
            )
        )
    }
}
```

### Type White-Listing

We can add types as "white-listed". This means that this type is considered as present in the system for any definition:

```kotlin
class NiaAppModuleCheck {

    @Test
    fun checkKoinModule() {

        // Verify Koin configuration
        niaAppModule.verify(
            // List types used in definitions but not declared directly (like parameter injection)
            extraTypes = listOf(MyType::class ...)
        )
    }
}
```

### Using Annotations for Verification

Annotations from `koin-core-annotations` help Koin infer injection contracts and validate configurations. Instead of having a complex DSL configuration, this helps identify those elements:

```kotlin
// indicates that "a" is an injected parameter
class ComponentB(@InjectedParam val a: ComponentA)
// indicates that "a" is dynamically provided
class ComponentBProvided(@Provided val a: ComponentA)
```

This helps prevent subtle issues during testing or runtime without writing custom verification logic.

---

## CheckModules API (Deprecated)

:::warning
The `checkModules()` API is deprecated since Koin 4.0. Use `verify()` instead, or migrate to the Koin Compiler Plugin for compile-time safety.
:::

The `checkModules()` function launches your modules and tries to run each possible definition.

```kotlin
class CheckModulesTest : KoinTest {

    @Test
    fun verifyKoinApp() {

        koinApplication {
            modules(module1, module2)
            checkModules()
        }
    }
}
```

Or using `checkKoinModules`:

```kotlin
class CheckModulesTest : KoinTest {

    @Test
    fun verifyKoinApp() {
        checkKoinModules(listOf(module1, module2))
    }
}
```

### CheckModule DSL

For any definition using injected parameters, properties or dynamic instances:

* `withInstance(value)` - will add `value` instance to Koin graph
* `withInstance<MyType>()` - will add a mocked instance of `MyType` (requires MockProviderRule)
* `withParameter<Type>(qualifier){ qualifier -> value }` - will add `value` instance to be injected as parameter
* `withProperty(key, value)` - add property to Koin

### Mocking with a JUnit Rule

To use mocks with `checkModules`, provide a `MockProviderRule`:

```kotlin
@get:Rule
val mockProvider = MockProviderRule.create { clazz ->
    // Mock with your framework here given clazz
    Mockito.mock(clazz.java)
}
```

### Verifying Modules with Dynamic Behavior

```kotlin
val myModule = module {
    factory { (id: String) -> FactoryPresenter(id) }
}
```

Verify with:

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

### Android Example

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

### Providing Scope Links

Link scopes using `withScopeLink`:

```kotlin
val myModule = module {
    scope(named("scope1")) {
        scoped { ComponentA() }
    }
    scope(named("scope2")) {
        scoped { ComponentB(get()) }
    }
}

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

---

## Migration Path

Both verification APIs will be replaced by the Koin Compiler Plugin's compile-time safety feature:

| Current | Future |
|---------|--------|
| `module.verify()` | Compiler Plugin (automatic) |
| `checkModules()` | Compiler Plugin (automatic) |
| Runtime verification | Compile-time verification |
| Manual test setup | No test code needed |

When the Compiler Plugin compile-time safety is available, you'll get dependency validation at build time without writing any verification tests.

See [Compiler Plugin Setup](/docs/setup/compiler-plugin) for setup instructions.
