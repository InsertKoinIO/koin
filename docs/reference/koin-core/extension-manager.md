---
title: Extension Manager
---

Here is a brief description of `KoinExtension` manager, deidcated to add new features inside Koin framework.

## Defining an extension

A Koin extension consist in having a class inhereting from `KoinExtension` interface:

```kotlin
interface KoinExtension {
    
    var koin : Koin
    
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