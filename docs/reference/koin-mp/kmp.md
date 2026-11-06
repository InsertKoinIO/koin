---
title: KMP Platform Integration
description: Use the Android Context from shared code, call Koin from Swift, and start Koin on desktop and web targets of a Kotlin Multiplatform app.
---

This page covers the platform details of a Kotlin Multiplatform app with Koin: using the Android `Context` from shared code, calling Koin from Swift, and the desktop and web targets. It assumes your project follows [KMP Setup](/docs/reference/koin-core/kmp-setup) and [KMP Sharing Patterns](/docs/reference/koin-core/kmp-shared-modules).

Complete projects: the [Compose Multiplatform sample](https://github.com/InsertKoinIO/koin-getting-started/tree/main/compose) (Compiler Plugin DSL) and the [annotations sample](https://github.com/InsertKoinIO/koin-getting-started/tree/main/compose-annotations).

## Android Context in shared code

Shared code can't use `android.content.Context`, but some shared classes need it on Android (to open files, read preferences). Wrap it in a platform type that common code can depend on:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
// commonMain
interface ContextWrapper {
    fun getContextInfo(): String
}

class FileStore(private val ctx: ContextWrapper)

val storageModule = module {
    single<FileStore>()
}
```

```kotlin
// androidMain
class AndroidContextWrapper(val context: Context) : ContextWrapper {
    override fun getContextInfo() = "Android: ${context.packageName}"
}

actual fun platformModule(): Module = module {
    single<AndroidContextWrapper>() bind ContextWrapper::class
}
```

```kotlin
// iosMain
class IOSContextWrapper : ContextWrapper {
    override fun getContextInfo() = "iOS"
}

actual fun platformModule(): Module = module {
    single<IOSContextWrapper>() bind ContextWrapper::class
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
// commonMain
interface ContextWrapper {
    fun getContextInfo(): String
}

@Singleton
class FileStore(private val ctx: ContextWrapper)
```

```kotlin
// androidMain
@Singleton
class AndroidContextWrapper(val context: Context) : ContextWrapper {
    override fun getContextInfo() = "Android: ${context.packageName}"
}
```

```kotlin
// iosMain
@Singleton
class IOSContextWrapper : ContextWrapper {
    override fun getContextInfo() = "iOS"
}
```

The platform classes are found by the `actual` platform module with `@ComponentScan` (see [KMP Sharing Patterns](/docs/reference/koin-core/kmp-shared-modules)).

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
// commonMain
interface ContextWrapper {
    fun getContextInfo(): String
}

class FileStore(private val ctx: ContextWrapper)

val storageModule = module {
    singleOf(::FileStore)
}
```

```kotlin
// androidMain
actual fun platformModule(): Module = module {
    singleOf(::AndroidContextWrapper) bind ContextWrapper::class
}

// iosMain
actual fun platformModule(): Module = module {
    singleOf(::IOSContextWrapper) bind ContextWrapper::class
}
```

</TabItem>
</Tabs>

`FileStore` is compiled once in `commonMain` and receives the platform wrapper. Only one Android definition touches `Context`, and it resolves once `androidContext()` is set at start. Put the Android-specific operations behind methods of `ContextWrapper` rather than checking the platform type in common code.

## iOS and Swift

### Retrieving dependencies from Swift

Swift can't call Koin's reified functions such as `get<T>()`. At the Swift boundary, write a small Kotlin helper in `iosMain` that implements `KoinComponent`, and use it from Swift. This is an interop entry point: your shared classes keep receiving their dependencies through their constructors.

```kotlin
// iosMain
class GreetingHelper : KoinComponent {
    private val greeting: Greeting by inject()
    fun greet(): String = greeting.greeting()
}
```

```swift
struct ContentView: View {
    let greet = GreetingHelper().greet()

    var body: some View {
        Text(greet)
    }
}
```

Koin must be started before the helper is used: see the iOS entry point in [KMP Setup](/docs/reference/koin-core/kmp-setup). `KoinComponent` is described in [KoinComponent](/docs/reference/koin-core/koin-component).

### Declaring a Swift instance in Koin

Sometimes a Swift class implements a Kotlin interface, and Kotlin code needs it from Koin. Register the instance at runtime with `Koin.declare()`. Swift passes the type as an Objective-C class or protocol, which this `iosMain` helper converts to a Kotlin `KClass`:

```kotlin
// iosMain
@OptIn(BetaInteropApi::class)
fun Koin.declareFromSwift(
    instance: Any,
    bindTo: ObjCObject,
    qualifier: Qualifier? = null,
    allowOverride: Boolean = true
) {
    val kClass: KClass<*> = when (bindTo) {
        is ObjCClass -> getOriginalKotlinClass(bindTo)
        is ObjCProtocol -> getOriginalKotlinClass(bindTo)
        else -> null
    } ?: error("Can't resolve Kotlin KClass from $bindTo")

    declare(
        instance = instance,
        qualifier = qualifier,
        secondaryTypes = listOf(kClass),
        allowOverride = allowOverride
    )
}
```

```swift
koin.declareFromSwift(
    instance: SwiftAnalytics(),
    bindTo: Analytics.self,
    qualifier: nil,
    allowOverride: true
)
```

`getOriginalKotlinClass()` comes from `kotlin.native` and maps a class (`.self` on a class) or a protocol (`.self` on a protocol) back to its Kotlin type. Declared instances are not checked at compile time: a Kotlin consumer fails at runtime if Swift has not declared the instance yet. The helper is based on a [proposal by @SarahDelCastillo](https://github.com/InsertKoinIO/koin/issues/1108#issuecomment-3645990426).

### Threading

Koin runs on the current Kotlin/Native memory model (the default since Kotlin 1.7.20). You can resolve dependencies and use scopes from any thread, with no freezing or `@SharedImmutable` annotation.

## Desktop (JVM)

Start Koin in `main()`, before the first window:

```kotlin
// jvmMain
fun main() = application {
    initKoin {
        printLogger()
        modules(desktopModule)
    }

    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

val desktopModule = module {
    single<DesktopFileManager>()
}
```

The `config` parameter of the shared `initKoin` adds desktop-only modules and the logger. Desktop is a JVM target, so JVM-only features such as `fileProperties()` are available (see [Starting Koin](/docs/reference/koin-core/starting-koin)).

## Web (JS and Wasm)

Start Koin in the `main()` of the `js` or `wasmJs` source set:

```kotlin
// jsMain or wasmJsMain
fun main() {
    initKoin {
        modules(webModule)
    }
    // start your UI
}

val webModule = module {
    single<BrowserStorage>()
}
```

With Compose Multiplatform for web, you can start Koin from the root Composable with `KoinApplication` instead (see [Koin for Compose](/docs/reference/koin-compose/compose)).

## Related

- [KMP Setup](/docs/reference/koin-core/kmp-setup): dependencies, `initKoin`, entry points.
- [KMP Sharing Patterns](/docs/reference/koin-core/kmp-shared-modules): platform modules and `expect`/`actual` definitions.
- [KoinComponent](/docs/reference/koin-core/koin-component): retrieve dependencies from classes Koin does not create.
- [Koin for Compose](/docs/reference/koin-compose/compose): Compose Multiplatform integration.
- [Testing](/docs/reference/koin-test/testing): test shared modules in `commonTest`.
