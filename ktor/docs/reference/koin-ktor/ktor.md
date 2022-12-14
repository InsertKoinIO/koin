---
title: Koin for Ktor
---

:::note
 The koin-ktor project is updated for Ktor `2.0.1`
:::


The `koin-ktor` project is dedicated to bring dependency injection for Ktor.

## Install Koin & inject

To start a Koin container in Ktor, just install the `Koin` plugin like follow:

```kotlin
fun Application.main() {
    // Install Ktor features
    install(Koin) {
        slf4jLogger()
        modules(helloAppModule)
    }

}
```

:::note
 You can also start it from outside of Ktor, but you won't be compatible with `autoreload` feature.
:::


### Injection in Application

Koin `inject()` and `get()` functions are available from `Application` class:

```kotlin
fun Application.main() {
    //...

    // Lazy inject HelloService
    val service by inject<HelloService>()

    // Routing section
    routing {
        get("/hello") {
            call.respondText(service.sayHello())
        }
    }
}
```

### Declaring Koin for a Ktor Module

For a Ktor module, you can load specific Koin modules. Just declare them with `koin { }` function:


```kotlin
fun Application.module2() {
    koin {
        // load appModule2 for module2 Ktor module
        modules(appModule2)
    }

}
```


### Injecting in Routing & Route 

Koin `inject()` and `get()` functions are available from `Route` & `Routing` classes:

```kotlin
fun Routing.v1() {

    // Lazy inject HelloService from within a Ktor Routing Node
    val service by inject<HelloService>()

    get("/v1/hello") {
        call.respondText("[/v1/hello] " + service.sayHello())
    }
}

```

From `Route` class:

```kotlin
fun Route.hello() {

    // Lazy inject HelloService from within a Ktor Route
    val service by inject<HelloService>()

    get("/v1/bye") {
        call.respondText("[/v1/bye] " + service.sayHello())
    }
}

```

### Events

You can listen to KTor Koin events:

```kotlin
fun Application.main() {
    // ...

    // Install Ktor features
    environment.monitor.subscribe(KoinApplicationStarted) {
        log.info("Koin started.")
    }

    environment.monitor.subscribe(KoinApplicationStopPreparing) {
        log.info("Koin stopping...")
    }

    environment.monitor.subscribe(KoinApplicationStopped) {
        log.info("Koin stopped.")
    }

    //...
}
```

