---
title: Extension Manager
---

# Extension Manager

Koin provides an extension system that allows you to add new features to the framework. This is useful for integrating Koin with external systems or adding custom functionality.

## KoinExtension

A Koin extension is a class implementing the `KoinExtension` interface:

```kotlin
interface KoinExtension {
    /**
     * Called when the extension is registered
     */
    fun onRegister(koin: Koin)

    /**
     * Called when Koin is closing
     */
    fun onClose()
}
```

### Creating an Extension

```kotlin
class MyCustomExtension : KoinExtension {
    private lateinit var koin: Koin

    override fun onRegister(koin: Koin) {
        this.koin = koin
        // Initialize your extension
    }

    override fun onClose() {
        // Cleanup resources
    }

    fun doSomething() {
        // Your extension logic
    }
}
```

### Registering an Extension

Use the `ExtensionManager` to register extensions:

```kotlin
fun KoinApplication.myExtension() {
    with(koin.extensionManager) {
        if (getExtensionOrNull<MyCustomExtension>(EXTENSION_ID) == null) {
            registerExtension(EXTENSION_ID, MyCustomExtension())
        }
    }
}

private const val EXTENSION_ID = "my-extension"
```

### Accessing an Extension

```kotlin
val Koin.myExtension: MyCustomExtension
    get() = extensionManager.getExtension(EXTENSION_ID)

// Usage
val extension = getKoin().myExtension
extension.doSomething()
```

### Using in Koin Setup

```kotlin
startKoin {
    myExtension()  // Register extension
    modules(appModule)
}
```

:::note
The `ExtensionManager` is marked as `@KoinInternalApi`. This means the API may change between versions. Use with caution in production code.
:::

## ResolutionExtension

For more advanced use cases, Koin provides `ResolutionExtension` to hook into the dependency resolution process. This allows you to provide instances from external sources.

```kotlin
interface ResolutionExtension {
    /**
     * Extension name for identification
     */
    val name: String

    /**
     * Called during dependency resolution
     * @param scope Current resolution scope
     * @param instanceContext Resolution context with type info
     * @return Instance if found, null otherwise
     */
    fun resolve(scope: Scope, instanceContext: ResolutionContext): Any?
}
```

### Use Cases

- Integrating with external DI containers
- Providing instances from a cache or pool
- Dynamic instance resolution based on runtime conditions
- Testing with mock providers

### Example: External Instance Provider

```kotlin
class ExternalInstanceProvider : ResolutionExtension {
    private val externalInstances = mutableMapOf<KClass<*>, Any>()

    override val name: String = "external-provider"

    override fun resolve(scope: Scope, instanceContext: ResolutionContext): Any? {
        return externalInstances[instanceContext.clazz]
    }

    fun <T : Any> registerInstance(clazz: KClass<T>, instance: T) {
        externalInstances[clazz] = instance
    }
}
```

### Registering a ResolutionExtension

```kotlin
val externalProvider = ExternalInstanceProvider()
externalProvider.registerInstance(MyService::class, MyServiceImpl())

startKoin {
    // Register the resolution extension
    koin.addResolutionExtension(externalProvider)

    modules(module {
        // This can now resolve MyService from the external provider
        single<MyComponent>()  // MyComponent depends on MyService
    })
}
```

:::warning Experimental API
The `ResolutionExtension` API is marked as `@KoinExperimentalAPI`. The API may change in future versions.
:::

### Complete Example

```kotlin
@OptIn(KoinExperimentalAPI::class)
fun resolutionExtensionExample() {
    val resolutionExtension = object : ResolutionExtension {
        val instanceMap = mapOf<KClass<*>, Any>(
            ComponentA::class to ComponentA()
        )

        override val name: String = "custom-resolver"

        override fun resolve(
            scope: Scope,
            instanceContext: ResolutionContext
        ): Any? {
            return instanceMap[instanceContext.clazz]
        }
    }

    val koin = koinApplication {
        printLogger(Level.DEBUG)
        koin.addResolutionExtension(resolutionExtension)
        modules(module {
            // ComponentB depends on ComponentA
            // ComponentA will be resolved from the extension
            single { ComponentB(get()) }
        })
    }.koin

    val componentB = koin.get<ComponentB>()
    // componentB.a is the instance from resolutionExtension
}
```

## When to Use Extensions

| Extension Type | Use Case |
|---------------|----------|
| `KoinExtension` | Adding features to Koin (logging, monitoring, custom scopes) |
| `ResolutionExtension` | Providing instances from external sources during resolution |

## Best Practices

1. **Use sparingly** - Extensions add complexity; prefer standard Koin definitions when possible
2. **Document your extensions** - Make it clear what the extension does and how to use it
3. **Handle cleanup** - Always implement `onClose()` to avoid resource leaks
4. **Consider thread safety** - Extensions may be called from multiple threads

## Next Steps

- **[Scopes](/docs/reference/koin-core/scopes)** - Custom scope management
- **[Modules](/docs/reference/koin-core/modules)** - Module organization
- **[Advanced Patterns](/docs/reference/koin-core/advanced-patterns)** - More advanced patterns
