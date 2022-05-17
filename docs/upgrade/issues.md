---
title: Known Issues
---

## Breakings 🚧

### 2.2.x

#### Core

KoinComponent and linked extension have been moved to package `org.koin.core.component`. Please reimport related APIs (`KoinComponent`,`inject`, `get`...) and also use `@KoinApiExtension`.

:::info
 The `@KoinApiExtension` annotation has been removed in 3.x branch version
:::

#### Android

Android ViewModel State API has also been broken and is back in 3.x. In the last version you can use `stateViewModel()` functions. 
