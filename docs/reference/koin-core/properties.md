---
title: Properties
---

Koin can handle properties from environment or external property file, to help you inject values into your definitions.

## Loading properties at start

You can load several type of properties at start:

* environment properties - load *system* properties
* koin.properties file - load properties from `/src/main/resources/koin.properties` file
* "extra" start properties - map of values passed at `startKoin` function

## Read property from a module

Be sure to load properties at Koin start:

```kotlin
startKoin {
    // Load properties from the default location
    // (i.e. `/src/main/resources/koin.properties`)
    fileProperties()
}
```

In a Koin module, you can get a property by its key:

in /src/main/resources/koin.properties file
```java
// Key - value
server_url=http://service_url
```

Just load it with `getProperty` function:

```kotlin
val myModule = module {

    // use the "server_url" key to retrieve its value
    single { MyService(getProperty("server_url")) }
}
```

