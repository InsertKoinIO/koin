---
title: Ktor & Koin Isolated Context
---

The `koin-ktor` module is dedicated to bring dependency injection for Ktor. 


## Isolated Koin Context Plugin

To start an Isolated Koin container in Ktor, just install the `KoinIsolated` plugin like follow:

```kotlin
fun Application.main() {
    // Install Koin plugin
    install(KoinIsolated) {
        slf4jLogger()
        modules(helloAppModule)
    }
}
```

:::warning
 By using an isolated Koin context you won't be able to use Koin outside Ktor server instance (i.e: by using `GlobalContext` for example)
:::



