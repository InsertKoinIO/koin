---
title: Scopes in Koin Annotations
---

While using definitions and modules, you may need to define scopes for a particular space and time resolution.

## Defining a Scope with @Scope

Koin allows the use of scopes. Please refer to [Koin Scopes](/docs/reference/koin-core/scopes) section for more details on basics. 

To declare a scope with annotations, just use `@Scope` annotation on a class, like this

```kotlin
@Scope
class MyScopeClass
```

> this will be equivalent of the following scope section:
> ```kotlin
> scope<MyScopeClass> {
> 
>}
> ```

Else, if you need a scope name more than a type, you need to tag a class with `@Scope(name = )` annotation, using `name` parameter:

```kotlin
@Scope(name = "my_scope_name")
class MyScopeClass
```

> this will be the equivalent of 
>
>```kotlin
>scope<named("my_scope_name")> {
>
>}
>```

## Adding a definition in a Scope with @Scoped

To declare a definition inside a scope (defined or not with annotations), just tag a class with `@Scope` and `@Scoped` annotations:

```kotlin
@Scope(name = "my_scope_name")
@Scoped
class MyScopedComponent
```

This will generate the right definition inside the scope section:

```kotlin
scope<named("my_scope_name")> {
  scoped { MyScopedComponent() }
}
```

:::info
  You need both annotations to indicate the needed scope space (with `@Scope`) and the kind of component to define (with `@Scoped`)
:::

## Dependency resolution from a scope

From a scoped definition, you can resolve any definition from your inner Scope and from the parent scopes.

For example, the following case will work:

```kotlin
@Single
class MySingle

@Scope(name = "my_scope_name")
@Scoped
class MyScopedComponent(
  val mySingle : MySingle,
  val myOtherScopedComponent :MyOtherScopedComponent
)

@Scope(name = "my_scope_name")
@Scoped
class MyOtherScopedComponent(
  val mySingle : MySingle
)
```

The component `MySingle` is defined as `single` definition, in the root. `MyScopedComponent` and `MyOtherScopedComponent` are defined in scope "my_scope_name".
The dependencies resolution from `MyScopedComponent` is accessing the Koin root with `MySingle` instance, and `MyOtherScopedComponent` scoped instance from the current "my_scope_name" scope.


## Resolving outside a Scope with @ScopeId (since 1.3.0)

You may need to resolve a component from another scope that is not directly accessible to your scope. For this, you need to tag your dependency with `@ScopeId` annotation to tell Koin to find this dependency in the scope of the given scope Id.

```kotlin
@Factory
class MyFactory(
  @ScopeId("my_scope_id") val myScopedComponent :MyScopedComponent
)
```

The above code is equivalent is generated:

```kotlin
factory { Myfactory(getScope("my_scope_id").get()) }
```

This example shows that `MyFactory` component will resolve `MyScopedComponent` component from a scope instance with id "my_scope_id". This scope, created with id "my_scope_id" needs to be created with the right scope definition.

:::info
  The `MyScopedComponent` component needs to be defined in a Scope section, and a scope instance needs to be created with id "my_scope_id". 
:::

## Scope Archetype Annotations

Koin Annotations provides predefined scope archetype annotations for common scope patterns, eliminating the need to manually declare scope types. These annotations combine scope declaration and component definition in a single annotation.

### Android Scope Archetypes

For Android development, you can use these predefined scope annotations:

#### @ActivityScope

Declare a component in an Activity scope:

```kotlin
@ActivityScope
class ActivityScopedComponent(val dependency: MyDependency)
```

This generates:
```kotlin
activityScope {
    scoped { ActivityScopedComponent(get()) }
}
```

**Usage:** The tagged class is meant to be used with Activity and the `activityScope` function to activate the scope.

#### @ActivityRetainedScope

Declare a component in an Activity Retained scope (survives configuration changes):

```kotlin
@ActivityRetainedScope
class RetainedComponent(val repository: MyRepository)
```

This generates:
```kotlin
activityRetainedScope {
    scoped { RetainedComponent(get()) }
}
```

**Usage:** The tagged class is meant to be used with Activity and the `activityRetainedScope` function to activate the scope.

#### @FragmentScope

Declare a component in a Fragment scope:

```kotlin
@FragmentScope
class FragmentScopedComponent(val service: MyService)
```

This generates:
```kotlin
fragmentScope {
    scoped { FragmentScopedComponent(get()) }
}
```

**Usage:** The tagged class is meant to be used with Fragment and the `fragmentScope` function to activate the scope.

### Core Scope Archetypes

#### @ViewModelScope

Declare a component in a ViewModel scope. This annotation is **Kotlin Multiplatform (KMP) compatible** and works with both Android ViewModels and Compose Multiplatform ViewModels:

```kotlin
@ViewModelScope
class ViewModelScopedRepository(val apiService: ApiService)

@ViewModelScope  
class ViewModelScopedUseCase(
    val repository: ViewModelScopedRepository,
    val analytics: AnalyticsService
)
```

This generates:
```kotlin
viewModelScope {
    scoped { ViewModelScopedRepository(get()) }
    scoped { ViewModelScopedUseCase(get(), get()) }
}
```

**Usage:** The tagged class is meant to be used with ViewModel and the `viewModelScope` function to activate the scope.

**KMP Support:** Works seamlessly across all Kotlin Multiplatform targets including Android, iOS, Desktop, and Web platforms where ViewModels are used.

### Using Scope Archetypes

Scope archetype annotations work seamlessly with regular Koin scoping:

```kotlin
// Regular components
@Single
class GlobalService

// Scoped components using archetypes
@ActivityScope
class ActivityService(val global: GlobalService)

@FragmentScope  
class FragmentService(
    val global: GlobalService,
    val activity: ActivityService
)
```

### Combining with Function Definitions

Scope archetypes can also be used on functions within modules:

```kotlin
@Module
class MyModule {
    
    @ActivityScope
    fun activityComponent(dep: MyDependency) = MyActivityComponent(dep)
    
    @FragmentScope
    fun fragmentComponent(dep: MyDependency) = MyFragmentComponent(dep)
}
```

:::info
Scope archetype annotations automatically create the appropriate scope definition and scoped component declaration, reducing boilerplate code for common scope patterns.
:::