---
title: Android Instrumented Testing
---

## Override production modules

Unlike [unit tests](../koin-test/testing.md), where you effectively call start Koin in each test class (i.e. `startKoin` or `KoinTestExtension`), in Instrumented tests Koin is started by your `Application` class. 

For overriding production Koin modules, `loadModules` and `unloadModules` are often unsafe because the changes are not applied immediately. Instead,the recommended approach is to add a `module` of your overrides to `modules` used by `startKoin` in the application's `Application` class.

```
startKoin {
    ...
    modules(productionModule, instrumentedTestModule)
    ...
}
```
