# Koin Annotations Inventory

This document provides a comprehensive inventory of all Koin annotations, their parameters, behaviors, and usage examples.

## Table of Contents

- [Definition Annotations](#definition-annotations)
  - [@Single](#single)
  - [@Factory](#factory)
  - [@Scoped](#scoped)
- [Scope Annotations](#scope-annotations)
  - [@Scope](#scope)
  - [@ViewModelScope](#viewmodelscope)
  - [@ActivityScope](#activityscope)
  - [@ActivityRetainedScope](#activityretainedscope)
  - [@FragmentScope](#fragmentscope)
  - [@ScopeId](#scopeid)
- [ViewModel & Android-Specific Annotations](#viewmodel--android-specific-annotations)
  - [@KoinViewModel](#koinviewmodel)
  - [@KoinWorker](#koinworker)
- [Qualifier Annotations](#qualifier-annotations)
  - [@Named](#named)
  - [@Qualifier](#qualifier)
- [Property Annotations](#property-annotations)
  - [@Property](#property)
  - [@PropertyValue](#propertyvalue)
- [Module & Application Annotations](#module--application-annotations)
  - [@Module](#module)
  - [@ComponentScan](#componentscan)
  - [@Configuration](#configuration)
  - [@KoinApplication](#koinapplication)
- [Monitoring Annotations](#monitoring-annotations)
  - [@Monitor](#monitor)
- [Meta Annotations (Internal)](#meta-annotations-internal)
  - [@ExternalDefinition](#externaldefinition)
  - [@MetaDefinition](#metadefinition)
  - [@MetaModule](#metamodule)
  - [@MetaApplication](#metaapplication)

---

## Definition Annotations

### @Single

**Package:** `org.koin.core.annotation`

**Target:** `CLASS`, `FUNCTION`

**Description:** Declares a type or function as a `single` (singleton) definition in Koin. A single instance is created and shared across the application.

**Parameters:**
- `binds: Array<KClass<*>> = [Unit::class]` - Explicit types to bind to this definition. Supertypes are automatically detected.
- `createdAtStart: Boolean = false` - If `true`, the instance is created when Koin starts.

**Behavior:**
All dependencies are filled by constructor injection.

**Example:**
```kotlin
@Single
class MyClass(val d : MyDependency)
```

**Generated Koin DSL:**
```kotlin
single { MyClass(get()) }
```

**With explicit binding:**
```kotlin
@Single(binds = [MyInterface::class])
class MyClass(val d : MyDependency) : MyInterface
```

**With creation at start:**
```kotlin
@Single(createdAtStart = true)
class MyClass(val d : MyDependency)
```

---

### @Factory

**Package:** `org.koin.core.annotation`

**Target:** `CLASS`, `FUNCTION`

**Description:** Declares a type or function as a `factory` definition in Koin. A new instance is created each time it is requested.

**Parameters:**
- `binds: Array<KClass<*>> = [Unit::class]` - Explicit types to bind to this definition. Supertypes are automatically detected.

**Behavior:**
All dependencies are filled by constructor injection. Each request creates a new instance.

**Example:**
```kotlin
@Factory
class MyClass(val d : MyDependency)
```

**Generated Koin DSL:**
```kotlin
factory { MyClass(get()) }
```

---

### @Scoped

**Package:** `org.koin.core.annotation`

**Target:** `CLASS`, `FUNCTION`

**Description:** Declares a type or function as a `scoped` definition in Koin. Must be associated with `@Scope` annotation. Instance is shared within a specific scope.

**Parameters:**
- `binds: Array<KClass<*>> = [Unit::class]` - Explicit types to bind to this definition. Supertypes are automatically detected.

**Behavior:**
Creates a scoped instance that lives within the defined scope's lifetime.

**Example:**
```kotlin
@Scope(MyScope::class)
@Scoped
class MyClass(val d : MyDependency)
```

**See Also:** [@Scope](#scope)

---

## Scope Annotations

### @Scope

**Package:** `org.koin.core.annotation`

**Target:** `CLASS`, `FUNCTION`

**Description:** Declares a class in a Koin scope. Scope name is described by either value (class) or name (string). By default, declares a `scoped` definition. Can be overridden with `@Scoped`, `@Factory`, `@KoinViewModel` annotations for explicit bindings.

**Parameters:**
- `value: KClass<*> = Unit::class` - Scope class value
- `name: String = ""` - Scope string value

**Behavior:**
Creates a scope definition associated with the specified scope type or name.

**Example with class:**
```kotlin
@Scope(MyScope::class)
class MyClass(val d : MyDependency)
```

**Generated Koin DSL:**
```kotlin
scope<MyScope> {
    scoped { MyClass(get()) }
}
```

**Example with string name:**
```kotlin
@Scope(name = "my_custom_scope")
class MyClass(val d : MyDependency)
```

---

### @ViewModelScope

**Package:** `org.koin.core.annotation`

**Target:** `CLASS`, `FUNCTION`

**Description:** Declares a class in a ViewModelScope Koin scope. This is a scope archetype for components that should live within a ViewModel's lifecycle.

**Parameters:** None

**Behavior:**
Creates a scoped definition within the `viewModelScope`.

**Example:**
```kotlin
@ViewModelScope
class MyClass(val d : MyDependency)
```

**Generated Koin DSL:**
```kotlin
viewModelScope {
    scoped { MyClass(get()) }
}
```

**Usage:**
The tagged class is meant to be used with ViewModel and `viewModelScope` function to activate the scope.

---

### @ActivityScope

**Package:** `org.koin.android.annotation`

**Target:** `CLASS`, `FUNCTION`

**Description:** Declares a class in an Activity Koin Scope.

**Parameters:** None

**Behavior:**
Creates a scoped definition within the `activityScope`.

**Example:**
```kotlin
@ActivityScope
class MyClass(val d : MyDependency)
```

**Generated Koin DSL:**
```kotlin
activityScope {
    scoped { MyClass(get()) }
}
```

**Usage:**
The tagged class is meant to be used with Activity and `activityScope` function to activate the scope.

---

### @ActivityRetainedScope

**Package:** `org.koin.android.annotation`

**Target:** `CLASS`, `FUNCTION`

**Description:** Declares a class in an Activity Koin scope, but retained across configuration changes.

**Parameters:** None

**Behavior:**
Creates a scoped definition within the `activityRetainedScope`.

**Example:**
```kotlin
@ActivityRetainedScope
class MyClass(val d : MyDependency)
```

**Generated Koin DSL:**
```kotlin
activityRetainedScope {
    scoped { MyClass(get()) }
}
```

**Usage:**
The tagged class is meant to be used with Activity and `activityRetainedScope` function to activate the scope.

---

### @FragmentScope

**Package:** `org.koin.android.annotation`

**Target:** `CLASS`, `FUNCTION`

**Description:** Declares a class in a Fragment Koin scope.

**Parameters:** None

**Behavior:**
Creates a scoped definition within the `fragmentScope`.

**Example:**
```kotlin
@FragmentScope
class MyClass(val d : MyDependency)
```

**Generated Koin DSL:**
```kotlin
fragmentScope {
    scoped { MyClass(get()) }
}
```

**Usage:**
The tagged class is meant to be used with Fragment and `fragmentScope` function to activate the scope.

---

### @ScopeId

**Package:** `org.koin.core.annotation`

**Target:** `VALUE_PARAMETER`

**Description:** Annotates a parameter from class constructor or function to request resolution for a given scope with Scope ID.

**Parameters:**
- `value: KClass<*> = Unit::class` - Scope type
- `name: String = ""` - Scope string identifier

**Behavior:**
Resolves the dependency from a specific scope identified by type or name.

**Example with string name:**
```kotlin
@Factory
class MyClass(@ScopeId(name = "my_scope_id") val d : MyDependency)
```

**Generated Koin DSL:**
```kotlin
factory { MyClass(getScope("my_scope_id").get()) }
```

**Example with type:**
```kotlin
@Factory
class MyClass(@ScopeId(MyScope::class) val d : MyDependency)
```

---

## ViewModel & Android-Specific Annotations

### @KoinViewModel

**Package:** `org.koin.android.annotation`

**Target:** `CLASS`, `FUNCTION`

**Description:** ViewModel annotation for Koin definition. Declares a type or function as a `viewModel` definition in Koin.

**Platform Support:**
- ✅ Android
- ✅ Kotlin Multiplatform (KMP)
- ✅ Compose Multiplatform (CMP)

**Parameters:**
- `binds: Array<KClass<*>> = []` - Explicit types to bind to this definition. Supertypes are automatically detected.

**Behavior:**
All dependencies are filled by constructor injection. Creates a ViewModel instance managed by Koin. Works across all platforms including Android, iOS, Desktop, and Web when using Compose Multiplatform.

**Example (Android/CMP):**
```kotlin
@KoinViewModel
class MyViewModel(val d : MyDependency) : ViewModel()
```

**Example (KMP/CMP shared):**
```kotlin
@KoinViewModel
class SharedViewModel(
    val repository: Repository,
    val analytics: Analytics
) : ViewModel()
```

**Generated Koin DSL:**
```kotlin
viewModel { MyViewModel(get()) }
```

---

### @KoinWorker

**Package:** `org.koin.android.annotation`

**Target:** `CLASS`, `FUNCTION`

**Description:** Worker annotation for Koin Definition. Declares a type as a `worker` definition for WorkManager workers.

**Parameters:**
- `binds: Array<KClass<*>> = []` - Explicit types to bind to this definition.

**Behavior:**
Creates a worker definition for Android WorkManager integration.

**Example:**
```kotlin
@KoinWorker
class MyWorker() : Worker()
```

---

## Qualifier Annotations

### @Named

**Package:** `org.koin.core.annotation`

**Target:** `CLASS`, `FUNCTION`, `VALUE_PARAMETER`

**Description:** Defines a qualifier for a given definition. Generates `StringQualifier("...")` or type-based qualifier.

**Parameters:**
- `value: String = ""` - String qualifier
- `type: KClass<*> = Unit::class` - Class qualifier

**Behavior:**
Used to distinguish between multiple definitions of the same type.

**Example with string:**
```kotlin
@Single
@Named("special")
class MyClass(val d : MyDependency)
```

**Usage in parameter:**
```kotlin
@Single
class Consumer(@Named("special") val myClass: MyClass)
```

**Example with type:**
```kotlin
@Single
@Named(type = MyType::class)
class MyClass(val d : MyDependency)
```

---

### @Qualifier

**Package:** `org.koin.core.annotation`

**Target:** `CLASS`, `FUNCTION`, `VALUE_PARAMETER`

**Description:** Defines a qualifier for a given definition. Similar to `@Named` but with reversed parameter priority.

**Parameters:**
- `value: KClass<*> = Unit::class` - Class qualifier
- `name: String = ""` - String qualifier

**Behavior:**
Used to distinguish between multiple definitions of the same type.

**Example:**
```kotlin
@Single
@Qualifier(name = "special")
class MyClass(val d : MyDependency)
```

---

## Property Annotations

### @Property

**Package:** `org.koin.core.annotation`

**Target:** `VALUE_PARAMETER`

**Description:** Annotates a constructor parameter or function parameter to resolve as a Koin property.

**Parameters:**
- `value: String` - Property name

**Behavior:**
Resolves the parameter value from Koin properties instead of dependency injection.

**Example:**
```kotlin
@Factory
class MyClass(@Property("name") val name : String)
```

**Generated Koin DSL:**
```kotlin
factory { MyClass(getProperty("name")) }
```

**With default value:**
```kotlin
@PropertyValue("name")
val defaultName = "MyName"

@Factory
class MyClass(@Property("name") val name : String)
```

**Generated Koin DSL:**
```kotlin
factory { MyClass(getProperty("name", defaultName)) }
```

---

### @PropertyValue

**Package:** `org.koin.core.annotation`

**Target:** `FIELD`

**Description:** Annotates a field value that will be a Property default value.

**Parameters:**
- `value: String` - Property name

**Behavior:**
Defines a default value for a property that can be used when the property is not found.

**Example:**
```kotlin
@PropertyValue("name")
val defaultName = "MyName"

@Factory
class MyClass(@Property("name") val name : String)
```

**Generated Koin DSL:**
```kotlin
factory { MyClass(getProperty("name", defaultName)) }
```

---

## Module & Application Annotations

### @Module

**Package:** `org.koin.core.annotation`

**Target:** `CLASS`

**Description:** Class annotation to help gather definitions inside a Koin module. Each function can be annotated with a Koin definition annotation.

**Parameters:**
- `includes: Array<KClass<*>> = []` - Module classes to include
- `createdAtStart: Boolean = false` - If `true`, module instances are created at start

**Behavior:**
Gathers all annotated functions and classes within the module.

**Example:**
```kotlin
@Module
class MyModule {
    @Single
    fun myClass(d : MyDependency) = MyClass(d)
}
```

**Generated Koin DSL:**
```kotlin
val MyModule.module = module {
    val moduleInstance = MyModule()
    single { moduleInstance.myClass(get()) }
}
```

**With includes:**
```kotlin
@Module(includes = [OtherModule::class])
class MyModule {
    // definitions
}
```

---

### @ComponentScan

**Package:** `org.koin.core.annotation`

**Target:** `CLASS`, `FIELD`

**Description:** Gathers definitions declared with Koin definition annotations. Scans current package or explicit package names.

**Parameters:**
- `value: vararg String = []` - Packages to scan (supports glob patterns)

**Behavior:**
Scans specified packages for annotated classes. Supports both exact package names and glob patterns.

**Glob Pattern Support:**

1. **Exact package names (no wildcards):**
   - `com.example.service` - Scans package and all subpackages (equivalent to `com.example**`)

2. **Multi-level scan including root:**
   - `com.example**` - Scans `com.example` and all subpackages

3. **Multi-level scan excluding root:**
   - `com.example.**` - Scans only subpackages of `com.example`, excludes root

4. **Single-level wildcard:**
   - `com.example.*.service` - Matches exactly one level (e.g., `com.example.user.service`)

5. **Combined wildcards:**
   - `com.**.service.*data` - Complex pattern matching
   - `com.*.service.**` - Scans subpackages under pattern

**Example - scan current package:**
```kotlin
@ComponentScan
class MyApp
```

**Example - scan specific packages:**
```kotlin
@ComponentScan("com.example.services", "com.example.repositories")
class MyApp
```

**Example - with glob patterns:**
```kotlin
@ComponentScan("com.example.**", "org.app.*.services")
class MyApp
```

---

### @Configuration

**Package:** `org.koin.core.annotation`

**Target:** `CLASS`, `FIELD`

**Description:** Applied to `@Module` class to associate it with one or more configurations (tags/flavors).

**Parameters:**
- `value: vararg String = []` - Configuration names

**Behavior:**
Modules can be grouped into configurations for conditional loading.

**Default Configuration:**
```kotlin
@Module
@Configuration
class MyModule
```
This module is part of the "default" configuration.

**Multiple Configurations:**
```kotlin
@Module
@Configuration("prod", "test")
class MyModule
```
This module is available in both "prod" and "test" configurations.

**With Default:**
```kotlin
@Module
@Configuration("default", "test")
class MyModule
```
Available in default and test configurations.

**Note:** `@Configuration("default")` is equivalent to `@Configuration`

---

### @KoinApplication

**Package:** `org.koin.core.annotation`

**Target:** `CLASS`

**Description:** Tags a class as a Koin application entry point. Generates Koin application bootstrap with `startKoin()` or `koinApplication()` functions.

**Parameters:**
- `configurations: Array<String> = []` - List of configuration names to scan
- `modules: Array<KClass<*>> = [Unit::class]` - List of modules to load besides configurations

**Behavior:**
Generates bootstrap functions that scan for configurations and included modules.

**Example - default configuration:**
```kotlin
@KoinApplication
class MyApp
```

**Generated functions:**
```kotlin
MyApp.startKoin()
MyApp.koinApplication()
```

**Example - specific configurations:**
```kotlin
@KoinApplication(configurations = ["default", "prod"])
class MyApp
```

**Example - with modules:**
```kotlin
@KoinApplication(
    configurations = ["default"],
    modules = [CoreModule::class, ApiModule::class]
)
class MyApp
```

**Usage with custom configuration:**
```kotlin
MyApp.startKoin {
    printLogger()
    // additional configuration
}
```

---

## Monitoring Annotations

### @Monitor

**Package:** `org.koin.core.annotation`

**Target:** `CLASS`, `FUNCTION`

**Description:** Marks a class or function for automatic monitoring and performance tracing through Kotzilla Platform, the official tooling platform for Koin.

**Parameters:** None

**Behavior:**
- When applied to a class: Generates a Koin proxy that monitors all public method calls
- When applied to a function: Monitors that specific method within a Koin-managed component
- Automatically captures performance metrics, error rates, and usage patterns
- Sends data to Kotzilla workspace for real-time analysis

**Requirements:**
- `implementation 'io.kotzilla:kotzilla-core:latest.version'`
- Valid Kotzilla Platform account and API key

**Example:**
```kotlin
@Monitor
class UserService(private val userRepository: UserRepository) {
    fun findUser(id: String): User? = userRepository.findById(id)
}
```

**Resources:**
- [Kotzilla Platform](https://kotzilla.io)
- [Complete Documentation](https://doc.kotzilla.io)
- [Latest Version](https://doc.kotzilla.io/docs/releaseNotes/changelogSDK)

**Since:** Kotzilla 1.2.1

---

## Meta Annotations (Internal)

These annotations are for internal use only by the Koin compiler and code generation.

### @ExternalDefinition

**Package:** `org.koin.meta.annotations`

**Target:** `CLASS`, `FIELD`, `FUNCTION`

**Description:** Internal usage for components discovery in generated package.

**Parameters:**
- `value: String = ""` - Package of declared definition

---

### @MetaDefinition

**Package:** `org.koin.meta.annotations`

**Target:** `CLASS`, `FUNCTION`, `PROPERTY`

**Description:** Meta Definition annotation to help represent definition metadata.

**Parameters:**
- `value: String = ""` - Definition full path
- `moduleTagId: String = ""` - Module Tag + ID (format: "module_id:module_tag")
- `dependencies: Array<String> = []` - Parameters tags to check
- `binds: Array<String> = []` - Bound types
- `qualifier: String = ""` - Qualifier
- `scope: String = ""` - Scope where it's declared

---

### @MetaModule

**Package:** `org.koin.meta.annotations`

**Target:** `CLASS`

**Description:** Meta Module annotation to help represent module metadata.

**Parameters:**
- `value: String = ""` - Module full path
- `id: String = ""` - Module ID
- `includes: Array<String> = []` - Includes Module Tags to check
- `configurations: Array<String> = []` - Module Configurations to check
- `isObject: Boolean = false` - Whether the module is an object

---

### @MetaApplication

**Package:** `org.koin.meta.annotations`

**Target:** `CLASS`

**Description:** Meta Application annotation to help represent application metadata.

**Parameters:**
- `value: String = ""` - Application full path
- `includes: Array<String> = []` - Used Module Tags to check
- `configurations: Array<String> = []` - Used Configurations modules to check

---

## Deprecated Annotations

### @Singleton

**Package:** `org.koin.core.annotation`

**Status:** DEPRECATED - ERROR level

**Replacement:** Use `@Singleton` from `koin-jsr330` package instead

**Description:** Same as `@Single` but deprecated in favor of JSR-330 compliance.

---

## Summary Table

| Annotation | Package | Purpose | Common Use Case |
|------------|---------|---------|-----------------|
| `@Single` | `org.koin.core.annotation` | Singleton definition | Shared application services |
| `@Factory` | `org.koin.core.annotation` | Factory definition | Per-request instances |
| `@Scoped` | `org.koin.core.annotation` | Scoped definition | Scope-specific instances |
| `@Scope` | `org.koin.core.annotation` | Scope declaration | Custom scopes |
| `@ViewModelScope` | `org.koin.core.annotation` | ViewModel scope | ViewModel-scoped dependencies |
| `@ActivityScope` | `org.koin.android.annotation` | Activity scope | Activity-scoped dependencies |
| `@ActivityRetainedScope` | `org.koin.android.annotation` | Retained activity scope | Config-change surviving deps |
| `@FragmentScope` | `org.koin.android.annotation` | Fragment scope | Fragment-scoped dependencies |
| `@ScopeId` | `org.koin.core.annotation` | Scope resolution | Resolve from specific scope |
| `@KoinViewModel` | `org.koin.android.annotation` | ViewModel definition | Android/KMP/CMP ViewModels |
| `@KoinWorker` | `org.koin.android.annotation` | Worker definition | WorkManager workers |
| `@Named` | `org.koin.core.annotation` | String/type qualifier | Distinguish same-type beans |
| `@Qualifier` | `org.koin.core.annotation` | Type/string qualifier | Distinguish same-type beans |
| `@Property` | `org.koin.core.annotation` | Property injection | Configuration values |
| `@PropertyValue` | `org.koin.core.annotation` | Property default | Default config values |
| `@Module` | `org.koin.core.annotation` | Module declaration | Group definitions |
| `@ComponentScan` | `org.koin.core.annotation` | Package scanning | Auto-discover definitions |
| `@Configuration` | `org.koin.core.annotation` | Module configuration | Build variants/flavors |
| `@KoinApplication` | `org.koin.core.annotation` | App entry point | Bootstrap Koin |
| `@Monitor` | `org.koin.core.annotation` | Performance monitoring | Production monitoring |

---

**Document Version:** 1.0
**Last Updated:** 20-10-2025
**Koin Annotations Version:** 2.2.x+
