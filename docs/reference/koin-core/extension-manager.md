---
title: Extension Manager
---

Here is a brief description of `KoinExtension` manager, dedicated to add new features inside Koin framework.

## Defining an extension

A Koin extension consist in having a class inheriting from `KoinExtension` interface:

```kotlin
interface KoinExtension {

    fun onRegister(koin : Koin)

    fun onClose()
}
```

this interface allow to ensure you get passed a `Koin` instance, and the extension is called when Koin is closing.

## Starting an extension

To start an extension, just extend the right place of the system, and register it with `Koin.extensionManager`.

Below here is how we define the `coroutinesEngine` extension:

```kotlin
fun KoinApplication.coroutinesEngine() {
    with(koin.extensionManager) {
        if (getExtensionOrNull<KoinCoroutinesEngine>(EXTENSION_NAME) == null) {
            registerExtension(EXTENSION_NAME, KoinCoroutinesEngine())
        }
    }
}
```

Below here is how we call the `coroutinesEngine` extension:

```kotlin
val Koin.coroutinesEngine: KoinCoroutinesEngine get() = extensionManager.getExtension(EXTENSION_NAME)
```

## Resolver Engine & Resolution Extension

Koin's resolution algorithm has been reworked to be pluggable and extensible. The new CoreResolver and ResolutionExtension APIs allow integration with external systems or custom resolution logic.

Internally, resolution now traverses stack elements more efficiently, with cleaner propagation across scopes and parent hierarchies. This will fix many issues related to the linked scope walk-through and allow better integration of Koin in other systems.

See below a test demoing resolution extension:

```kotlin
@Test
fun extend_resolution_test(){
    val resolutionExtension = object : ResolutionExtension {
        val instanceMap = mapOf<KClass<*>, Any>(
            Simple.ComponentA::class to Simple.ComponentA()
        )

        override val name: String = "hello-extension"
        override fun resolve(
            scope: Scope,
            instanceContext: ResolutionContext
        ): Any? {
            return instanceMap[instanceContext.clazz]
        }
    }

    val koin = koinApplication{
        printLogger(Level.DEBUG)
        koin.resolver.addResolutionExtension(resolutionExtension)
        modules(module {
            single { Simple.ComponentB(get())}
        })
    }.koin

    assertEquals(resolutionExtension.instanceMap[Simple.ComponentA::class], koin.get<Simple.ComponentB>().a)
    assertEquals(1,koin.instanceRegistry.instances.values.size)
}
```