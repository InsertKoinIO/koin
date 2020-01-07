
## Starting Koin with Ktor

Start Koin from your `Application` with the Ktor extension below:

```kotlin
fun Application.main() {
    // Install Ktor features
    install(Koin) {
        // Use SLF4J Koin Logger at Level.INFO
        slf4jLogger()

        // declare used modules
        modules(helloAppModule)
    }
}
```

## Application, Route and Routing are KoinComponents

Easily access `KoinComponent` features from `Application`, `Route` and `Routing` Ktor classes:

```kotlin
fun Routing.v1() {

    // Lazy inject HelloService from within a Ktor Routing Node
    val service by inject<HelloService>()

    get("/v1/hello") {
        call.respondText("[/v1/hello] " + service.sayHello())
    }
}
```

## Declare components without function (Experimental feature)

Easily declare your components without any description function:

```kotlin
module(createdAtStart = true) {
    singleBy<HelloService, HelloServiceImpl>()
    single<HelloRepository>()
}
```

instead of 

```kotlin
module(createdAtStart = true) {
    singleBy<HelloService> { HelloServiceImpl(get()) }
    single { HelloRepository() } 
}
```

## Use Koin events

see [hello-ktor](../../../examples/hello-ktor) example

```kotlin
fun Application.main() {
    // ...

    // Install Ktor features
    environment.monitor.subscribe(KoinApplicationStarted) {
        log.info("Koin started.")
    }
    install(Koin) {
        // ...
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
