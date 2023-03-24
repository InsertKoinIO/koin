---
title: Compose & Multiplatform Basics
---

This page describe how you can inject your dependencies for your Jetpack & Jetbrains Compose app - https://www.jetbrains.com/lp/compose-mpp/

## Injecting into a @Composable

While writing your composable function, you gain access to the following Koin API:

* `koinInject()` - fetch instance from Koin container
* `getKoin()` - get current Koin instance

For a module that declares a 'MyService' component:

```kotlin
val androidModule = module {

    single { MyService() }
}
```

We can get your instance like that:

```kotlin
@Composable
fun App() {
    val myService = koinInject<MyService>()
}
```

:::note 
To keep aligned on the functional aspect of Jetpack Compose, the best writing approach is to inject instances directly into functions properties. This way allow to have default implementation with Koin, but keep open to inject instances how you want.
:::

```kotlin
@Composable
fun App(myService: MyService = koinInject()) {
}
```

## Koin Features for your @Composable (WIP)

- `koinInject` to inject Koin dependency into a Composable
- `KoinApplication` to create Koin application as a Composable
- `rememberKoinScope` and `KoinScope` to handle Koin Scope in a Composable, follow up current to close scope once Composable is ended
- `rememberKoinModules` load Koin modules and remember on current Composable
