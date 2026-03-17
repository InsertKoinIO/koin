---
title: Android Context & Qualifiers
---

This guide covers Android-specific qualifier patterns, particularly around Context handling.

:::info
For general qualifier concepts (named, type-safe, enums, JSR-330), see [Qualifiers](/docs/reference/koin-core/qualifiers).
:::

## Android Context - No Qualifiers Needed

Unlike Hilt, Koin automatically provides the Android Context without requiring qualifiers.

### Koin's Context Resolution

```kotlin
val androidModule = module {
    // Context is automatically available
    single {
        SharedPreferences(
            androidContext()  // Application context automatically provided
        )
    }

    single {
        NotificationManager(
            androidContext().getSystemService(Context.NOTIFICATION_SERVICE)
        )
    }
}
```

### No @ApplicationContext or @ActivityContext

**In Hilt, you need:**
```kotlin
// Hilt requires explicit qualifiers
class MyRepository @Inject constructor(
    @ApplicationContext private val context: Context
)
```

**In Koin, it's automatic:**
```kotlin
class MyRepository(
    private val context: Context  // Just use Context
)

val appModule = module {
    single { MyRepository(androidContext()) }
}
```

:::info
**Koin Advantage:** The `androidContext()` function always provides the Application context. No qualifiers needed to distinguish between Application and Activity contexts.
:::

## When You Need Activity Context

For cases where you need Activity context, don't inject it - use it directly:

```kotlin
class ScreenMetrics(private val activity: Activity) {
    fun getScreenSize(): Point {
        val display = activity.windowManager.defaultDisplay
        return Point().also { display.getSize(it) }
    }
}

// Don't define in modules - create directly in Activity
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val metrics = ScreenMetrics(this)  // Activity context used directly
    }
}
```

:::warning
**Best Practice:** Avoid injecting Activity context into long-lived objects. It causes memory leaks. Use Application context (`androidContext()`) for dependencies that outlive an Activity.
:::

## Qualified Android Dependencies

When you need multiple configurations of Android-specific dependencies:

```kotlin
val databaseModule = module {
    single(named("user_db")) {
        Room.databaseBuilder(
            androidContext(),
            UserDatabase::class.java,
            "user-database"
        ).build()
    }

    single(named("cache_db")) {
        Room.databaseBuilder(
            androidContext(),
            CacheDatabase::class.java,
            "cache-database"
        ).build()
    }
}
```

## Next Steps

- **[Qualifiers](/docs/reference/koin-core/qualifiers)** - Complete qualifier documentation
- **[Android Best Practices](/docs/reference/koin-android/best-practices)** - Memory management
- **[Android Scopes](/docs/reference/koin-android/scope)** - Lifecycle-aware scoping
