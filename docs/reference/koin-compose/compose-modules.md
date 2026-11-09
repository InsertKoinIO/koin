---
title: Dynamic Modules
description: Load Koin modules when a Composable enters the composition with rememberKoinModules, and unload them when it leaves.
---

This page covers `rememberKoinModules`, which loads Koin modules when a Composable enters the composition and can unload them when it leaves. Use it to load the definitions of a feature only when its screen is shown. You should know how to declare [modules](/docs/reference/koin-core/modules).

`rememberKoinModules` is in `io.insert-koin:koin-compose` and is marked `@KoinExperimentalAPI`.

## rememberKoinModules

```kotlin
import org.koin.compose.module.rememberKoinModules

val checkoutModule = module {
    factory<CheckoutRepository>()
    viewModel<CheckoutViewModel>()
}

@OptIn(KoinExperimentalAPI::class)
@Composable
fun CheckoutScreen() {
    rememberKoinModules(unloadOnForgotten = true) { listOf(checkoutModule) }

    val viewModel = koinViewModel<CheckoutViewModel>()
}
```

The lambda returns the modules to load. Koin loads them into the Koin instance of the composition the first time `CheckoutScreen` is composed. Recompositions don't load them again.

Call `rememberKoinModules` before the `koinInject()` or `koinViewModel()` calls that need its definitions.

## Unloading

By default, the modules stay loaded after the Composable leaves the composition. Three parameters control unloading:

| Parameter | Default | Effect |
|-----------|---------|--------|
| `unloadOnForgotten` | `unloadModules` | Unload when the Composable leaves the composition |
| `unloadOnAbandoned` | `unloadModules` | Unload if the composition is abandoned |
| `unloadModules` | `false` | Default value for both parameters above |

```kotlin
// unload in both cases
rememberKoinModules(unloadModules = true) { listOf(checkoutModule) }
```

Unloading removes the definitions and drops their `single` instances. Code that still holds a reference to an instance keeps it, but Koin can no longer resolve that type. Don't unload a module that another screen still uses.

## Loading per destination

Call `rememberKoinModules` in a navigation destination to load a feature when the user navigates to it:

```kotlin
NavHost(navController, startDestination = HomeRoute) {
    composable<HomeRoute> {
        HomeScreen()
    }
    composable<CheckoutRoute> {
        rememberKoinModules(unloadOnForgotten = true) { listOf(checkoutModule) }
        CheckoutScreen()
    }
}
```

## Module styles

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
val checkoutModule = module {
    factory<CheckoutRepository>()
    viewModel<CheckoutViewModel>()
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

`rememberKoinModules` takes `Module` values. Annotated `@Module` classes are loaded at start through `@KoinApplication` (see [Modules](/docs/reference/koin-core/modules)). To load a feature on demand, declare its module with the DSL.

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val checkoutModule = module {
    factoryOf(::CheckoutRepository)
    viewModelOf(::CheckoutViewModel)
}
```

</TabItem>
</Tabs>

## Limits

- **Main thread**: modules are loaded synchronously during composition. A large module adds to the time of that frame. To load modules in the background at start, see [Lazy Modules](/docs/reference/koin-core/lazy-modules).
- **Overrides**: a module loaded later wins over an earlier definition of the same type (see [Definitions](/docs/reference/koin-core/definitions)). Unloading it does not restore the earlier definition.
- **Previews**: `rememberKoinModules` needs a running Koin instance. For previews, use `KoinApplicationPreview` (see [Compose Testing](/docs/reference/koin-compose/compose-testing)).

## Related

- [Modules](/docs/reference/koin-core/modules): declare and compose modules.
- [Lazy Modules](/docs/reference/koin-core/lazy-modules): load modules in the background at start.
- [Starting Koin](/docs/reference/koin-core/starting-koin): load and unload modules outside Compose.
- [Scopes in Compose](/docs/reference/koin-compose/compose-scopes): instances bound to a Composable.
- [Lifecycle and State](/docs/reference/koin-compose/compose-lifecycle): what each Compose API releases and when.
