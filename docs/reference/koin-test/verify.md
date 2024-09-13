---
title: Verifying your Koin configuration
---

Koin allows you to verify your configuration modules, avoiding discovering dependency injection issues at runtime.

## Koin Configuration check with Verify() - JVM Only [3.3]

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
        niaAppModule.verify()
    }
}
```


Launch the JUnit test and you're done! ✅


As you may see, we use the extra Types parameter to list types used in the Koin configuration but not declared directly. This is the case for SavedStateHandle and WorkerParameters types, that are used as injected parameters. The Context is declared by androidContext() function at start.

The verify() API is ultra light to run and doesn't require any kind of mock/stub to run on your configuration.

## Verifying with Injected Parameters - JVM Only [4.0]

When you have a configuration that implies injected obects with `parametersOf`, the verification will fail because there is no definition of the parameter's type in your configuration. 
However you can define a parameter type, to be injected with given definition `definition<Type>(Class1::class, Class2::class ...)`.

Here is how it goes:

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

## Type White-Listing

We can add types as "white-listed". This means that this type is considered as present in the system for any definition. Here is how it goes:

```kotlin
class NiaAppModuleCheck {

    @Test
    fun checkKoinModule() {

        // Verify Koin configuration
        niaAppModule.verify(
            // List types used in definitions but not declared directly (like parameters injection)
            extraTypes = listOf(MyType::class ...)
        )
    }
}
```