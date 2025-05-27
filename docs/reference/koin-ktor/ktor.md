---
title: Dependency Injection in Ktor
---

The `koin-ktor` module is dedicated to bringing dependency injection for Ktor.

## Install Koin Plugin

To start a Koin container in Ktor, just install the `Koin` plugin as follows:

```kotlin
fun Application.main() {
    // Install Koin
    install(Koin) {
        slf4jLogger()
        modules(helloAppModule)
    }

}
```

### Compatible with Ktor's DI (4.1)

Koin 4.1 fully supports new Ktor 3.2!

We extracted CoreResolver to abstract resolution rules for Koin and allow extension with ResolutionExtension. We added new KtorDIExtension as Ktor ResolutionExtension to help Koin resolve Ktor default DI instance.

Koin Ktor plugin is automatically setting up Ktor DI integration. Below, see how you can consume Ktor dependencies from Koin:
```kotlin
// let's define a Ktor object
fun Application.setupDatabase(config: DbConfig) {
    // ...
    dependencies {
        provide<Database> { database }
    }
}
```

```kotlin
// let's inject it in a Koin definition
class CustomerRepositoryImpl(private val database: Database) : CustomerRepository

    fun Application.customerDataModule() {
        koinModule {
            singleOf(::CustomerRepositoryImpl) bind CustomerRepository::class
        }
}
```


## Inject in Ktor

Koin `inject()` and `get()` functions are available from `Application`, `Route`, and `Routing` classes:

```kotlin
fun Application.main() {

    // inject HelloService
    val service by inject<HelloService>()

    routing {
        get("/hello") {
            call.respondText(service.sayHello())
        }
    }
}
```

### Resolve from Ktor Request Scope (since 4.1)

You can declare components to live within the Ktor request scope timeline. For this, you just need to declare your component inside a `requestScope` section. Given a `ScopeComponent` class to instantiate on the Ktor web request scope, let's declare it:

```kotlin
requestScope {
    scopedOf(::ScopeComponent)
}
```

And from your HTTP call, simply call `call.scope.get()` to resolve the right dependency:

```kotlin
routing {
    get("/hello") {
        val component = call.scope.get<ScopeComponent>()
        // ... 
    }
}
```

This allows your scoped dependency to resolve `ApplicationCall` as scope's source of your resolution. You can inject it directly into constructor:

```kotlin
class ScopeComponent(val call : ApplicationCall) {
}
```

:::note
For each new request, the scope will be recreated. This creates and drops scope instances for each request
:::


### Declare Koin modules in Ktor module (4.1)

Use `Application.koinModule {}` or `Application.koinModules()` directly within your app setup to declare new modules within your Ktor module:

```kotlin
fun Application.customerDataModule() {
    koinModule {
        singleOf(::CustomerRepositoryImpl) bind CustomerRepository::class
    }
}
```

### Ktor Events

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

