---
title: Managing Decompose Scopes
---

The `koin-decompose` module is dedicated to manage scopes inside the Decompose components. Works pretty the same as [Android setup](/reference/koin-android/scope)

## Scope for Decompose components

### Declare a Decompose Scope

To scope dependencies on an Decompose component, you have to declare a scope section with the `scope` block like follow:

```kotlin
class MyUseCase()
class MyDelegate(val useCase : MyUseCase)

module {
  // Declare scope for MyComponent
  scope<MyComponent> {
    // get MyDelegate instance from current scope 
    scoped { MyDelegate(get()) }
    scoped { MyUseCase() }
  }
}
```

### Decompose Scope Interface

Under the hood, Decompose scopes needs to be used with `DecomposeScopeComponent` interface to implement `scope` field like this:

```kotlin
class MyComponent(
    context: ComponentContext,
) : ComponentContext by context, DecomposeScopeComponent {

    override val scope: Scope by componentScope()
}
```

We need to use the `DecomposeScopeComponent` interface and implement the `scope` property. This will setting up the default scope used by your class.

### Decompose Scope API

To create a Koin scope bound to a Decompose component, just use the following function:
- `createComponentScope()` - Create Scope for current Component (scope is not linked to the parent component)

This function is available as delegate, to implement scope:

- `componentScope()` - Create Scope for current Component (scope is not linked to the parent component)

### DecomposeScopeComponent and Scope closing handling

You can run some code before Koin Scope is being destroyed, by overriding `onCloseScope` function from `DecomposeScopeComponent`:

```kotlin
class MyComponent(
    context: ComponentContext,
) : ComponentContext by context, DecomposeScopeComponent {

    override val scope: Scope by componentScope()

    override fun onCloseScope() {
        // Called before closing the Scope
    }
}
```
