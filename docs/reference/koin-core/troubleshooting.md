---
title: Troubleshooting
---

# Troubleshooting

This guide covers debugging, common errors, and anti-patterns to avoid.

## Circular Dependencies

### Problem

```kotlin
// Circular dependency - will fail at runtime
class ServiceA(val serviceB: ServiceB)
class ServiceB(val serviceA: ServiceA)

module {
    single { ServiceA(get()) }
    single { ServiceB(get()) }  // ERROR: Circular dependency!
}
```

### Solution 1: Lazy Injection

Break the cycle with lazy resolution:

```kotlin
class ServiceA : KoinComponent {
    val serviceB: ServiceB by inject()  // Lazy
}

class ServiceB : KoinComponent {
    val serviceA: ServiceA by inject()  // Lazy
}

module {
    single { ServiceA() }
    single { ServiceB() }
}
```

### Solution 2: Extract Shared Dependency

Refactor to remove the cycle (recommended):

```kotlin
// Extract shared logic
@Singleton
class SharedService

@Singleton
class ServiceA(private val shared: SharedService)

@Singleton
class ServiceB(private val shared: SharedService)
```

### Solution 3: Use an Interface

```kotlin
interface ServiceBContract {
    fun doSomething()
}

@Singleton
class ServiceA(private val serviceB: ServiceBContract)

@Singleton
class ServiceB(private val serviceA: ServiceA) : ServiceBContract
```

## Debugging

### Enable Logging

```kotlin
startKoin {
    // Set log level
    printLogger(Level.DEBUG)  // DEBUG, INFO, ERROR, NONE

    modules(appModule)
}
```

### Verify Modules with `verify()`

Validate all definitions can be resolved:

```kotlin
// In tests
@Test
fun `verify all modules`() {
    appModule.verify()  // Fails if any dependency is missing
}
```

:::info
Both `verify()` and `checkModules()` will be replaced by native compile-time safety in the Koin Compiler Plugin. See [Module Verification](/docs/reference/koin-test/verify) for details.
:::

## Common Errors

**Missing Definition:**
```
No definition found for class 'UserRepository'
```
Fix: Add the missing definition to a module

**Circular Dependency:**
```
Circular dependency detected
```
Fix: Use lazy injection or refactor (see above)

**Scope Not Found:**
```
No scope definition found for 'MyScope'
```
Fix: Ensure scope is created before accessing scoped dependencies

**Multiple Definitions:**
```
Multiple definitions found for type 'ApiClient'
```
Fix: Use qualifiers to distinguish between definitions

## Common Anti-Patterns

### 1. Service Locator Overuse

```kotlin
// Bad - Service locator pattern
class UserViewModel : ViewModel(), KoinComponent {
    fun loadUser() {
        val repository = get<UserRepository>()  // Manual resolution
        // ...
    }
}

// Good - Constructor injection
class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {
    fun loadUser() {
        // Dependencies already injected
    }
}
```

### 2. God Modules

```kotlin
// Bad - Everything in one module
val appModule = module {
    // 100+ definitions here
}

// Good - Organized modules
val databaseModule = module { /* ... */ }
val networkModule = module { /* ... */ }
val homeModule = module { /* ... */ }
```

### 3. Excessive Qualifiers

```kotlin
// Bad - Qualifiers for different types
module {
    single(named("user_repository")) { UserRepository() }
    single(named("order_repository")) { OrderRepository() }
}

// Good - Types distinguish themselves
module {
    singleOf(::UserRepository)
    singleOf(::OrderRepository)
}
```

### 4. Mixing Concerns

```kotlin
// Bad - Side effects in module
module {
    single {
        println("Loading database...")  // Side effect
        Database()
    }
}

// Good - Pure dependency creation
module {
    single { Database() }
}
```

### 5. Hidden Dependencies

```kotlin
// Bad - Dependencies hidden inside
class UserService {
    private val api = ApiClient()  // Hidden dependency
}

// Good - Explicit dependencies
class UserService(private val api: ApiClient)
```

## Best Practices Summary

1. **Prefer constructor injection** - Avoid `get()` calls inside classes
2. **Use `verify()` in tests** - Catch missing definitions early
3. **Keep modules focused** - Single responsibility per module
4. **Avoid circular dependencies** - Refactor or use lazy injection
5. **Use qualifiers sparingly** - Only when same type has multiple instances

## Next Steps

- **[Modules](/docs/reference/koin-core/modules)** - Module organization
- **[Testing](/docs/reference/koin-test/testing)** - Testing with Koin
- **[Scopes](/docs/reference/koin-core/scopes)** - Managing lifecycle
