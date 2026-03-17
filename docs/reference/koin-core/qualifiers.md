---
title: Qualifiers
---

# Qualifiers

Qualifiers allow you to differentiate between multiple definitions of the same type in your Koin modules.

## When You Need Qualifiers

You need qualifiers when:
- You have multiple implementations of the same interface
- You need different configurations of the same type
- You want to distinguish between instances with different purposes

```kotlin
// Without qualifiers - conflict!
val networkModule = module {
    single { OkHttpClient.Builder()...build() }
    single { OkHttpClient.Builder()...build() }  // Which one?
}
```

## Named Qualifiers

Use `named()` to distinguish between definitions:

### Defining

```kotlin
import org.koin.core.qualifier.named

val networkModule = module {
    single(named("encrypted")) {
        OkHttpClient.Builder()
            .addInterceptor(EncryptionInterceptor())
            .build()
    }

    single(named("logging")) {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor())
            .build()
    }
}
```

### Injecting

```kotlin
// In module definitions
val apiModule = module {
    single {
        ApiService(
            encryptedClient = get(named("encrypted")),
            loggingClient = get(named("logging"))
        )
    }
}

// With KoinComponent
class MyService : KoinComponent {
    private val encryptedClient: OkHttpClient by inject(named("encrypted"))
}
```

### With Annotations

```kotlin
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
@Named("encrypted")
class EncryptedHttpClient : OkHttpClient()

@Single
@Named("logging")
class LoggingHttpClient : OkHttpClient()

@Single
class ApiService(
    @Named("encrypted") private val encryptedClient: OkHttpClient,
    @Named("logging") private val loggingClient: OkHttpClient
)
```

:::note
For Compiler Plugin DSL and Classic DSL autowire (`singleOf`, `factoryOf`), qualifiers cannot be automatically resolved. Use Classic DSL with lambdas or Annotations when definitions require qualifiers.
:::

## Type-Safe Qualifiers

### Using Types

Use `named<T>()` with any type as a qualifier:

```kotlin
// Define qualifier types
object EncryptedClient
object LoggingClient

val networkModule = module {
    single(named<EncryptedClient>()) {
        OkHttpClient.Builder()
            .addInterceptor(EncryptionInterceptor())
            .build()
    }

    single(named<LoggingClient>()) {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor())
            .build()
    }
}

// Inject
val client: OkHttpClient = get(named<EncryptedClient>())
```

### Using Enums

For better IDE support, use enums:

```kotlin
enum class NetworkClient {
    ENCRYPTED,
    LOGGING,
    FAST
}

val networkModule = module {
    single(named(NetworkClient.ENCRYPTED)) {
        OkHttpClient.Builder()
            .addInterceptor(EncryptionInterceptor())
            .build()
    }

    single(named(NetworkClient.LOGGING)) {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor())
            .build()
    }
}

// Inject - typo impossible!
val client: OkHttpClient = get(named(NetworkClient.ENCRYPTED))
```

**Benefits of type-safe qualifiers:**
- Compile-time type safety
- No string typos
- IDE autocomplete and refactoring support

## JSR-330 @Qualifier

Koin supports standard JSR-330 `@Qualifier` annotations:

```kotlin
import jakarta.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EncryptedClient

// Use in definitions
@Single
@IoDispatcher
fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

// Use in injection
@Single
class MyRepository(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
)
```

## Common Use Cases

### Multiple API Versions

```kotlin
val networkModule = module {
    single(named("api_v1")) {
        Retrofit.Builder()
            .baseUrl("https://api.example.com/v1/")
            .build()
    }

    single(named("api_v2")) {
        Retrofit.Builder()
            .baseUrl("https://api.example.com/v2/")
            .build()
    }
}
```

### Different Timeout Configurations

```kotlin
val networkModule = module {
    single(named("fast")) {
        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .build()
    }

    single(named("slow")) {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }
}
```

### Environment Configurations

```kotlin
val configModule = module {
    single(named("debug")) {
        AppConfig(apiUrl = "https://dev.example.com", loggingEnabled = true)
    }

    single(named("release")) {
        AppConfig(apiUrl = "https://api.example.com", loggingEnabled = false)
    }

    // Select based on environment
    single<AppConfig> {
        if (isDebug) get(named("debug")) else get(named("release"))
    }
}
```

## Best Practices

### 1. Use Qualifiers Sparingly

```kotlin
// Good - qualifiers only when necessary
val appModule = module {
    single { UserRepository(get()) }  // No qualifier needed
    single { AuthRepository(get()) }   // Different types
}

// Over-qualification - avoid
val appModule = module {
    single(named("user_repository")) { UserRepository(get()) }
    single(named("auth_repository")) { AuthRepository(get()) }
    // Unnecessary - types are already different
}
```

### 2. Prefer Type Differentiation

```kotlin
// Better - use different types
interface EncryptedHttpClient
interface LoggingHttpClient

class EncryptedOkHttpClient : OkHttpClient(), EncryptedHttpClient
class LoggingOkHttpClient : OkHttpClient(), LoggingHttpClient

val networkModule = module {
    single<EncryptedHttpClient> { EncryptedOkHttpClient() }
    single<LoggingHttpClient> { LoggingOkHttpClient() }
}
```

### 3. Avoid Qualifier Chains

```kotlin
// Bad - complex qualifier dependencies
val badModule = module {
    single(named("a")) { A() }
    single(named("b")) { B(get(named("a"))) }
    single(named("c")) { C(get(named("b"))) }
}

// Better - flatten or use different types
val goodModule = module {
    single { A() }
    single { B(get()) }
    single { C(get()) }
}
```

### 4. Document Qualifiers

```kotlin
val networkModule = module {
    // Client for secure API calls with encryption
    single(named("encrypted")) { ... }

    // Client for debugging with full request/response logging
    single(named("logging")) { ... }
}
```

## Naming Conventions

### String-Based

```kotlin
// Good - descriptive, lowercase with underscores
single(named("encrypted_client")) { ... }
single(named("user_database")) { ... }
single(named("api_v2")) { ... }

// Avoid - unclear or inconsistent
single(named("client1")) { ... }  // What does "1" mean?
```

### Enum-Based

```kotlin
// Good - clear enum names
enum class DatabaseType {
    USER_DATA,
    CACHE,
    ANALYTICS
}

enum class ApiVersion {
    V1, V2, V3
}
```

## Common Pitfalls

### Forgetting Qualifiers on Injection

```kotlin
val module = module {
    single(named("encrypted")) { OkHttpClient() }
}

val repoModule = module {
    single {
        MyRepository(get())  // ❌ Error: No definition for OkHttpClient
        // Should be: get(named("encrypted"))
    }
}
```

### Mismatched Qualifier Names

```kotlin
val module = module {
    single(named("encrypted_client")) { OkHttpClient() }
}

val repoModule = module {
    single {
        ApiService(
            get(named("encrypted"))  // ❌ Typo! Should be "encrypted_client"
        )
    }
}
```

Use enum qualifiers to avoid typos!

## Next Steps

- **[Definitions](/docs/reference/koin-core/definitions)** - Definition types and binding
- **[Modules](/docs/reference/koin-core/modules)** - Module organization
- **[Injection](/docs/reference/koin-core/injection)** - Retrieving dependencies
