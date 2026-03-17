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
    dslSafetyChecks = true
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

### dslSafetyChecks

- **Type**: Boolean
- **Default**: `true`
- **Description**: Validates that DSL function calls (like `create()`) inside lambdas are the only instruction. Helps prevent common mistakes.
- **Usage**: Disable temporarily during migration from classic DSL if needed.

```kotlin
koinCompiler {
    dslSafetyChecks = false  // Disable during migration
}
```

## Complete Example

```kotlin
// build.gradle.kts
plugins {
    alias(libs.plugins.koin.compiler)
}

koinCompiler {
    userLogs = true        // Log component detection
    debugLogs = false      // Verbose logs (off by default)
    dslSafetyChecks = true // Validate DSL usage
}
```

## Best Practices

- **Enable `userLogs`** during development to see which components are detected
- **Keep `dslSafetyChecks` enabled** (default) for safer DSL usage
- **Use `debugLogs`** only when troubleshooting plugin issues

## See Also

- **[Compiler Plugin Setup](/docs/setup/compiler-plugin)** - Complete setup guide
- **[Starting with Annotations](/docs/reference/koin-annotations/start)** - Getting started
