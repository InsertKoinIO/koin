---
title: Context Isolation
---

Context isolation allows SDK makers to use Koin without conflicting with the host application's Koin instance.

:::info
For general Koin setup, see **[Starting Koin](/docs/reference/koin-core/starting-koin)**.
:::

## When to Use Context Isolation

- **SDK/Library Development** - Your library uses Koin internally
- **Avoiding Conflicts** - Host app may also use Koin
- **Encapsulation** - Keep your DI container private

## Creating an Isolated Context

Instead of using `startKoin` (which registers in GlobalContext), use `koinApplication`:

```kotlin
// Isolated Koin context for your SDK
object MySdkKoinContext {

    private val koinApp = koinApplication {
        modules(sdkModule)
    }

    val koin = koinApp.koin
}

val sdkModule = module {
    single<SdkService>()
    single<SdkRepository>()
}
```

## Custom KoinComponent

Create a custom `KoinComponent` that uses your isolated context:

```kotlin
internal interface SdkKoinComponent : KoinComponent {
    // Override to use isolated context
    override fun getKoin(): Koin = MySdkKoinContext.koin
}

// Usage in your SDK classes
class MySdkClass : SdkKoinComponent {
    private val service: SdkService by inject()  // Uses isolated context
}
```

## Testing Isolated Context

Override `getKoin()` in tests to use the isolated context:

```kotlin
class SdkTest : KoinTest {
    override fun getKoin(): Koin = MySdkKoinContext.koin

    @Before
    fun setUp() {
        val testModule = module {
            single<SdkService> { MockSdkService() }
        }
        koin.loadModules(listOf(testModule))
    }

    @After
    fun tearDown() {
        koin.unloadModules(listOf(testModule))
    }
}
```

## See Also

- **[Starting Koin](/docs/reference/koin-core/starting-koin)** - Standard Koin setup
- **[Compose Isolated Context](/docs/reference/koin-compose/isolated-context)** - Isolation in Compose apps
