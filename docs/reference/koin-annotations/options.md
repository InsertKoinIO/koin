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
    skipDefaultValues = true  // Use Kotlin defaults instead of DI resolution
    unsafeDslChecks = true    // Validate DSL usage
}
```

## Best Practices

- **Keep `compileSafety` enabled** (default) for compile-time dependency validation
- **Keep `skipDefaultValues` enabled** (default) to respect Kotlin default values
- **Enable `userLogs`** during development to see which components are detected
- **Keep `unsafeDslChecks` enabled** (default) for safer DSL usage
- **Use `debugLogs`** only when troubleshooting plugin issues

## See Also

- **[Compile-Time Safety](/docs/reference/koin-compiler/compile-safety)** - What gets validated and how
- **[Compiler Plugin Setup](/docs/setup/compiler-plugin)** - Complete setup guide
- **[Starting with Annotations](/docs/reference/koin-annotations/start)** - Getting started
