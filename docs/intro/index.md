---
title: Welcome
description: Map of the Koin documentation. Find where each topic lives, from first setup to testing, platforms and production.
---

Koin is a dependency injection framework for Kotlin. You declare how your classes are built in modules, Koin creates and wires the instances at runtime, and the optional Koin Compiler Plugin checks the whole graph when you build. It runs on Android, Compose, Kotlin Multiplatform, Ktor and any Kotlin/JVM application.

This page is a map of the documentation. Each section below says which question it answers and where to start.

```kotlin
class UserRepository(private val api: ApiService)

val appModule = module {
    single<ApiService>()
    single<UserRepository>()
}

fun main() {
    startKoin { modules(appModule) }
}
```

## I want to...

- Understand dependency injection first: [What is Dependency Injection?](/docs/intro/what-is-dependency-injection)
- Understand how Koin works in five minutes: [Core Concepts](/docs/intro/core-concepts)
- Pick between DSL and annotations: [Choosing your style](/docs/intro/choosing-your-style)
- Add Koin to my Gradle build: [Gradle Setup](/docs/setup/gradle)
- Inject a ViewModel in a Composable: [ViewModel in Compose](/docs/reference/koin-compose/compose-viewmodel)
- Inject in an Activity, Fragment or Service: [Android Entry Points](/docs/reference/koin-android/entry-points)
- Wire Retrofit, Room or OkHttp: [Android Library Integration](/docs/reference/koin-android/binding-patterns)
- Share modules between Android and iOS: [KMP Sharing Patterns](/docs/reference/koin-core/kmp-shared-modules)
- Pass a runtime value such as an ID to a definition: [Injected Parameters](/docs/reference/koin-core/injection-parameters)
- Test my Koin configuration: [Verifying your Koin configuration](/docs/reference/koin-test/verify) and [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety)
- Fix a "No definition found" error: [Troubleshooting](/docs/reference/koin-core/troubleshooting)
- Migrate from Hilt: [Migrating from Hilt](/docs/reference/koin-android/hilt-migration)
- Move my annotations from KSP to the Compiler Plugin: [Migrating from KSP](/docs/migration/from-ksp-to-compiler-plugin)
- See how Koin behaves in production: [Production Monitoring](/docs/tooling/production-monitoring)

## Where to go

### Get Started

What is Koin, how does it work, which style should I use?

- [What is Koin?](/docs/intro/what-is-koin): what Koin is and how it works, runtime and compile time.
- [Core Concepts](/docs/intro/core-concepts): definitions, modules, the container, injection, scopes.
- [Choosing your style](/docs/intro/choosing-your-style): annotations or the DSL (Compiler Plugin, Constructor or Classic DSL).
- [Koin Compiler Plugin](/docs/intro/koin-compiler-plugin): what the plugin does at build time.

### Setup

What do I add to my build?

- [Setup](/docs/setup): what to install for each platform.
- [Gradle Setup](/docs/setup/gradle): the BOM and every Koin artifact.
- [Compiler Plugin Setup](/docs/setup/compiler-plugin): install and configure the Gradle plugin.

### Tutorials

How do I build a first app with Koin, step by step?

- [Kotlin](/docs/quickstart/kotlin): a plain Kotlin application.
- [Android - ViewModel](/docs/quickstart/android-viewmodel) and [Android - Jetpack Compose](/docs/quickstart/android-compose).
- [Compose Multiplatform](/docs/quickstart/cmp): shared UI on Android and iOS.
- [Ktor](/docs/quickstart/ktor): a server application.

### Core

How do I declare, organize and retrieve dependencies on any platform?

- [Starting Koin](/docs/reference/koin-core/starting-koin): `startKoin`, logging, properties.
- [Modules](/docs/reference/koin-core/modules) and [Definitions](/docs/reference/koin-core/definitions): declare what Koin builds.
- [Scopes](/docs/reference/koin-core/scopes): instances tied to a lifecycle.
- [Troubleshooting](/docs/reference/koin-core/troubleshooting): errors and how to read them.

### Platforms

How do I use Koin with a specific platform or framework?

- Android: [Start Koin on Android](/docs/reference/koin-android/start), [Android ViewModel](/docs/reference/koin-android/viewmodel), [Multi-Module Android Apps](/docs/reference/koin-android/multi-module).
- Compose: [Koin for Compose](/docs/reference/koin-compose/compose), [ViewModel in Compose](/docs/reference/koin-compose/compose-viewmodel), [Navigation 3](/docs/reference/koin-compose/navigation3).
- Kotlin Multiplatform: [KMP Setup](/docs/reference/koin-core/kmp-setup), [Sharing Patterns](/docs/reference/koin-core/kmp-shared-modules).
- Ktor: [Koin for Ktor](/docs/reference/koin-ktor/ktor), [Request Scopes](/docs/reference/koin-ktor/ktor-scopes).

### Testing

How do I test code that uses Koin, and test the configuration itself?

- [Injecting in Tests](/docs/reference/koin-test/testing): `KoinTest`, replacing definitions with fakes.
- [Verifying your Koin configuration](/docs/reference/koin-test/verify): check modules in a unit test.
- [Android Instrumented Testing](/docs/reference/koin-android/instrumented-testing) and [Testing Composables](/docs/reference/koin-compose/compose-testing).

### Production & Tooling

Which tools help me write, inspect and run Koin apps?

- [Tooling](/docs/tooling): overview of the tools around Koin.
- [IDE Plugin](/docs/tooling/ide-plugin): navigation and checks in Android Studio and IntelliJ IDEA.
- [Monitor](/docs/tooling/monitor) and [Production Monitoring](/docs/tooling/production-monitoring): see what Koin does at runtime.

### Migration

I use another framework or an older Koin setup. How do I move?

- [Koin vs Hilt](/docs/intro/koin-vs-hilt): how the two compare.
- [Migrating from Hilt](/docs/reference/koin-android/hilt-migration): step-by-step guide.
- [JSR-330 Compatibility](/docs/reference/koin-android/jsr330): keep `@Inject` and `@Singleton` from `javax.inject`.
- [Migrating from KSP](/docs/migration/from-ksp-to-compiler-plugin): move annotations to the Compiler Plugin.

### Reference

What are all the keywords, annotations and options?

- [DSL Reference](/docs/reference/koin-core/dsl): every DSL keyword.
- [Annotations Reference](/docs/reference/koin-annotations/annotations-inventory): every annotation and its parameters.
- [Compiler Plugin Options](/docs/reference/koin-annotations/options): every Gradle option of the plugin.

### Support

Which version should I use, what changed, where do I get help?

- [Releases](/docs/support/releases): versions, changes and upgrade notes.
- [API Stability](/docs/support/api-stability): what stable, experimental and internal mean.
- [Support](/docs/support): roadmap, community and support options.

## Related

- [What is Koin?](/docs/intro/what-is-koin): the next page to read if you are new to Koin.
- [Core Concepts](/docs/intro/core-concepts): the mental model behind every other page.
- [Setup](/docs/setup): install Koin for your platform.
