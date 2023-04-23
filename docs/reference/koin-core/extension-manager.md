---
title: Extension Manager
---

Here is a brief description of `KoinExtension` manager, dedicated to adding new features inside the Koin framework.

## Defining an extension

A Koin extension consists of having a class inherit from the `KoinExtension` interface:

```kotlin
interface KoinExtension {
    
    var koin : Koin
    
    fun onClose()
}
```

this interface ensures that you get passed a `Koin` instance, and the extension is called when Koin is closing.

## Starting an extension

To start an extension, just extend the right place of the system, and register it with `Koin.extensionManager`.

Below is how we define the `coroutinesEngine` extension:

```kotlin
fun KoinApplication.coroutinesEngine() {
    with(koin.extensionManager) {
        if (getExtensionOrNull<KoinCoroutinesEngine>(EXTENSION_NAME) == null) {
            registerExtension(EXTENSION_NAME, KoinCoroutinesEngine())
        }
    }
}
```

Below is how we call the `coroutinesEngine` extension:

```kotlin
val Koin.coroutinesEngine: KoinCoroutinesEngine get() = extensionManager.getExtension(EXTENSION_NAME)
```