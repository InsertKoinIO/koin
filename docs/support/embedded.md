---
title: Koin Embedded
custom_edit_url: null
---

Koin Embedded is a new Koin project, targeting Android/Kotlin SDK & Library developers.

This project proposes scripts to help rebuild & package Koin project with a different package name. The interest is for SDK & Library development, to avoid conflict between embedded Koin version and any consuming application that would use another version of Koin, that might conflict.

Feedback or help? Contact [Koin Team](mailto:koin@kotzilla.io).

:::info
this initiative is currently in Beta, we are looking for feedback
:::

## Embedded Version (Beta)

Here is an example of Koin embedded version: [Kotzilla Repository](https://repository.kotzilla.io/#browse/browse:Koin-Embedded)
- Available packages: `embedded-koin-core`, `embedded-koin-android`
- Relocation on from `org.koin.*` to `embedded.koin.*`

Setup your Gradle config with this Maven repository:
```kotlin
maven { 'https://repository.kotzilla.io/repository/kotzilla-platform/' }
```

## Relocation Scripts (Beta)

Here is some scripts that help rebuild Koin for a given package name, helping to embed it and avoid conflict with regular usage of Koin framework.

Follow-up on Koin [relocation scripts](https://github.com/InsertKoinIO/koin-embedded?tab=readme-ov-file#koin-relocation-scripts) project for more details.