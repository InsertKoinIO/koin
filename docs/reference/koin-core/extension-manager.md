---
title: Extension Manager
description: Add features to a Koin container with KoinExtension, and provide instances from outside Koin during resolution with ResolutionExtension.
---

Koin has two extension points for library and framework authors. A `KoinExtension` attaches a feature to a Koin container, with a hook at registration and at close. A `ResolutionExtension` gives Koin another source of instances when no definition matches. Most applications never need either: use [definitions](/docs/reference/koin-core/definitions) first.

## KoinExtension

A `KoinExtension` is an object registered in the container under an id:

```kotlin
interface KoinExtension {
    fun onRegister(koin: Koin)
    fun onClose()
}
```

Koin calls `onRegister` when you register the extension, and `onClose` when the container closes (`stopKoin()` or `koinApplication.close()`).

Koin uses this mechanism itself: the coroutines engine behind [lazy modules](/docs/reference/koin-core/lazy-modules) is the `KoinCoroutinesEngine` extension.

### Writing an extension

```kotlin
class AuditExtension : KoinExtension {
    private lateinit var koin: Koin

    override fun onRegister(koin: Koin) {
        this.koin = koin
    }

    override fun onClose() {
        // release resources
    }

    fun record(event: String) { ... }
}
```

### Registering and reading it

Expose a function on `KoinApplication` to register the extension, and a property on `Koin` to read it back:

```kotlin
private const val AUDIT_EXTENSION = "audit"

fun KoinApplication.audit() {
    with(koin.extensionManager) {
        if (getExtensionOrNull<AuditExtension>(AUDIT_EXTENSION) == null) {
            registerExtension(AUDIT_EXTENSION, AuditExtension())
        }
    }
}

val Koin.audit: AuditExtension
    get() = extensionManager.getExtension(AUDIT_EXTENSION)
```

```kotlin
startKoin {
    audit()
    modules(appModule)
}

getKoin().audit.record("app_started")
```

| Function | Behavior |
|----------|----------|
| `registerExtension(id, extension)` | Stores the extension and calls `onRegister`. An extension with the same id is replaced |
| `getExtension<T>(id)` | Returns the extension. Fails with "Koin extension 'id' not found." if it is missing |
| `getExtensionOrNull<T>(id)` | Returns the extension, or `null` |

:::warning
`ExtensionManager` is marked `@KoinInternalApi`: you need to opt in, and the API may change between versions without a deprecation cycle.
:::

## ResolutionExtension

A `ResolutionExtension` is asked for an instance when nothing else matches: after the definitions of the current scope, the scope source and the linked scopes, including the root scope.

```kotlin
interface ResolutionExtension {
    val name: String
    fun resolve(scope: Scope, instanceContext: ResolutionContext): Any?
}
```

Return the instance for `instanceContext.clazz` (and `instanceContext.qualifier` if you use qualifiers), or `null` to let Koin continue and fail with `NoDefinitionFoundException`.

Koin uses this to read instances from Ktor's dependency injection in the [Ktor DI bridge](/docs/reference/koin-ktor/ktor-bridge).

### Example: instances from another container

```kotlin
class LegacyContainerResolution(
    private val legacy: Map<KClass<*>, Any>,
) : ResolutionExtension {

    override val name: String = "legacy-container"

    override fun resolve(scope: Scope, instanceContext: ResolutionContext): Any? =
        legacy[instanceContext.clazz]
}
```

Register it with `addResolutionExtension`, on the `koin` of the application:

```kotlin
@OptIn(KoinExperimentalAPI::class)
fun startApp(legacy: Map<KClass<*>, Any>) {
    startKoin {
        koin.addResolutionExtension(LegacyContainerResolution(legacy))
        modules(appModule)
    }
}
```

A definition in `appModule` that needs a type found only in the legacy map now gets it from the extension.

With the Compiler Plugin, a type that only an extension provides is unknown at build time, so compile-time safety reports it as missing. Mark it as provided: see [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety).

`addResolutionExtension` is marked `@KoinExperimentalAPI`.

## Limits

- **Internal and experimental APIs**: both extension points can change between Koin versions.
- **No lifecycle for resolution extensions**: Koin does not cache the instances a `ResolutionExtension` returns. Cache them yourself if needed.
- **Last in line**: a resolution extension is never asked when a definition matches. It cannot override a definition.
- **Thread safety**: `resolve` can be called from several threads at once.

## Related

- [Lazy Modules](/docs/reference/koin-core/lazy-modules): the feature built on the coroutines engine extension.
- [Ktor DI Bridge](/docs/reference/koin-ktor/ktor-bridge): a resolution extension in practice.
- [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety): declaring dependencies provided outside Koin.
- [Starting Koin](/docs/reference/koin-core/starting-koin): the application DSL where extensions are registered.
- [Scopes](/docs/reference/koin-core/scopes): resolution order inside a scope.
