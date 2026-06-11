---
title: R8 / ProGuard
---

This page explains how Koin behaves under code shrinking and obfuscation (R8 / ProGuard), what
Koin keeps for you, and what **you** need to keep in your own app.

## TL;DR

- **Koin's core resolution is R8-safe.** `get<T>()`, `inject<T>()`, and the `*Of` builders
  (`singleOf`, `factoryOf`, `viewModelOf`, …) resolve dependencies **at compile time** — they use
  reified types and, on Android/JVM, key the registry by `Class.getName()`. There is **no runtime
  reflection over your constructors**, so you do **not** need to keep your definitions, ViewModels,
  or their constructors on Koin's behalf.
- Koin ships `consumer-rules.pro` in its Android AARs (`koin-android`, `koin-core-viewmodel`,
  `koin-compose-viewmodel`, `koin-androidx-workmanager`, `koin-androidx-startup`), so the rules
  below are applied automatically — you usually don't add anything.
- You still need to keep classes that **something else** loads reflectively (see below).

## What Koin keeps for you (shipped consumer rules)

The AARs silence R8 warnings about Koin internals:

```proguard
-dontwarn org.koin.**
```

`koin-androidx-startup` additionally keeps its manifest-referenced initializer. None of these keep
your application classes — Koin doesn't need them kept.

## What you must keep

These come from the platform/libraries, not from Koin's resolution:

- **Fragments created by `KoinFragmentFactory`** are instantiated by class name. Keep your Fragment
  subclasses (they're usually kept already via `@Keep`, layout references, or AndroidX rules).
- **WorkManager `ListenableWorker` subclasses** are kept by `androidx.work`'s own consumer rules.
- **Saved state for process death.** `SavedStateHandle` is provided by androidx `CreationExtras`,
  not by Koin. The values you put into it must survive R8 like any other saved state — keep your
  own `@Parcelize` / `Serializable` state classes:

```proguard
# Example — keep your own saved-state payloads
-keep class com.example.** implements android.os.Parcelable { *; }
```

## ViewModels & SavedStateHandle (#2044)

A common belief is that intermittent `No definition found for SavedStateHandle` crashes are caused
by R8 stripping Koin's ViewModel reflection. **They are not** — `viewModelOf(::MyViewModel)` is
compile-time, so a `-keep` on your ViewModel won't change Koin's resolution.

`SavedStateHandle` is only available **while the ViewModel is being created** (it is built from the
`CreationExtras` passed to the factory). Resolve it **directly in the ViewModel constructor** — do
not resolve it lazily or after construction:

```kotlin
// ✅ resolved during creation
class MyViewModel(val handle: SavedStateHandle) : ViewModel()

// ❌ resolved later — the CreationExtras are gone by then
class MyViewModel(koin: Koin) : ViewModel() {
    val handle by lazy { koin.get<SavedStateHandle>() } // fails
}
```

If you declare ViewModels inside `viewModelScope { }`, enable the matching option so the scope can
be created:

```kotlin
startKoin {
    options(viewModelScopeFactory())
    modules(appModule)
}
```

## Non-Android targets (JS / WASM / Native)

On Android/JVM Koin keys the registry by `Class.getName()`, which is stable under R8. On
**Kotlin/JS, WASM, and Native**, Koin uses `qualifiedName` / `simpleName` from Kotlin reflection.
Aggressive name minification on those targets can affect type identity — prefer **named
qualifiers** (`named("...")`) over relying on class names when you minify non-Android targets.
