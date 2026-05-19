---
title: Compiler Plugin Options
---

The Koin Compiler Plugin supports configuration options to customize its behavior.

## Configuration

Configure the compiler plugin in your `build.gradle.kts`:

```kotlin
koinCompiler {
    userLogs = true
    debugLogs = false
    compileSafety = true
    strictSafety = true       // auto-detected by default
    skipDefaultValues = true
    unsafeDslChecks = true
}
```

## Available Options

### userLogs

- **Type**: Boolean
- **Default**: `false`
- **Description**: Enables logs for component detection and DSL/annotation processing. Shows which components are discovered and processed by the plugin.
- **Usage**: Enable during development to debug component discovery issues.

```kotlin
koinCompiler {
    userLogs = true
}
```

### debugLogs

- **Type**: Boolean
- **Default**: `false`
- **Description**: Enables verbose debug logs for internal plugin processing (FIR/IR phases, module discovery).
- **Usage**: Enable when troubleshooting plugin issues or reporting bugs.

```kotlin
koinCompiler {
    debugLogs = true
}
```

### compileSafety

- **Type**: Boolean
- **Default**: `true`
- **Description**: Enables compile-time dependency validation. When enabled, the plugin validates that all dependencies can be resolved at build time — catching missing definitions, qualifier mismatches, and broken call sites before runtime.
- **Usage**: Enabled by default. Disable temporarily if you need to bypass validation during migration.

```kotlin
koinCompiler {
    compileSafety = true
}
```

See [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety) for full details on what gets validated.

### strictSafety

- **Type**: Boolean
- **Default**: auto-detected (enabled on aggregator modules — those containing `startKoin`, `koinApplication`, or `@KoinApplication`)
- **Description**: Forces the full-graph safety pass (A3) to re-run on every build, bypassing Kotlin's incremental compilation cache on the aggregator module. Library and feature modules stay fully incremental.
- **Usage**: Leave at default. Set explicitly to `true` if auto-detection misses your aggregator, or to `false` to opt out (e.g. when a test fixture only references `startKoin` in a comment and trips the detector).

```kotlin
koinCompiler {
    strictSafety = true   // force-enable
    // or
    strictSafety = false  // opt out of auto-detection
}
```

**Why it exists**: K2's incremental compilation today (via the Build Tools API used by AGP) doesn't track two things the DI graph relies on — DSL definitions inside `module { … }` lambda bodies (not part of any declaration's ABI), and `@ComponentScan` package-scope discovery (no source-level edge from the scanner to newly-added classes). The aggregator's `compileKotlin` task can be marked UP-TO-DATE even when the graph changed. `strictSafety` is the smallest correct workaround on top of what K2 IC currently surfaces; it has bounded cost since only the aggregator re-runs each build.

Has no effect when `compileSafety = false`. See [koin-compiler-plugin issue #32](https://github.com/InsertKoinIO/koin-compiler-plugin/issues/32) for background.

### skipDefaultValues

- **Type**: Boolean
- **Default**: `true`
- **Description**: When enabled, parameters with Kotlin default values will use the default instead of being resolved from the DI container. Nullable parameters and annotated parameters (`@Named`, `@InjectedParam`, etc.) are still resolved normally.
- **Usage**: Enabled by default. Disable to always inject all parameters from the DI container.

```kotlin
koinCompiler {
    skipDefaultValues = true
}
```

### unsafeDslChecks

- **Type**: Boolean
- **Default**: `true`
- **Description**: Validates that DSL function calls (like `create()`) inside lambdas are the only instruction. Helps prevent common mistakes.
- **Usage**: Disable temporarily during migration from classic DSL if needed.

```kotlin
koinCompiler {
    unsafeDslChecks = false  // Disable during migration
}
```

## Complete Example

```kotlin
// build.gradle.kts
plugins {
    alias(libs.plugins.koin.compiler)
}

koinCompiler {
    userLogs = true           // Log component detection
    debugLogs = false         // Verbose logs (off by default)
    compileSafety = true      // Compile-time dependency validation
    strictSafety = true       // Force aggregator to re-run safety pass (auto-detected by default)
    skipDefaultValues = true  // Use Kotlin defaults instead of DI resolution
    unsafeDslChecks = true    // Validate DSL usage
}
```

## Best Practices

- **Keep `compileSafety` enabled** (default) for compile-time dependency validation
- **Leave `strictSafety` at auto-detect** — only override if the detector misses your aggregator or trips on a non-aggregator file
- **Keep `skipDefaultValues` enabled** (default) to respect Kotlin default values
- **Enable `userLogs`** during development to see which components are detected
- **Keep `unsafeDslChecks` enabled** (default) for safer DSL usage
- **Use `debugLogs`** only when troubleshooting plugin issues

## See Also

- **[Compile-Time Safety](/docs/reference/koin-compiler/compile-safety)** - What gets validated and how
- **[Compiler Plugin Setup](/docs/setup/compiler-plugin)** - Complete setup guide
- **[Starting with Annotations](/docs/reference/koin-annotations/start)** - Getting started
