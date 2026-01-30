---
title: Fragment Factory
---

Koin integrates with [AndroidX FragmentFactory](https://developer.android.com/reference/kotlin/androidx/fragment/app/FragmentFactory) to enable constructor injection in Fragments.

:::info
Fragment Factory uses DSL only. Annotation and Compiler Plugin DSL support is not yet available.
:::

## Setup

### Add Dependency

```groovy
implementation "io.insert-koin:koin-android:$koin_version"
```

### Configure Fragment Factory

In your Koin configuration, enable the fragment factory:

```kotlin
startKoin {
    androidContext(this@MainApplication)
    fragmentFactory()
    modules(appModule)
}
```

## Declaring Fragments

Use the `fragment` DSL keyword with constructor injection:

```kotlin
class MyFragment(
    private val myService: MyService
) : Fragment()

val appModule = module {
    single { MyService() }
    fragment { MyFragment(get()) }
}
```

## Using Fragments

### Setup in Activity

Call `setupKoinFragmentFactory()` **before** `super.onCreate()`:

```kotlin
class MyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Must be called BEFORE super.onCreate()
        setupKoinFragmentFactory()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
```

### Add Fragment

Use the reified extension function:

```kotlin
supportFragmentManager.beginTransaction()
    .replace<MyFragment>(R.id.container)
    .commit()
```

With arguments and tag:

```kotlin
supportFragmentManager.beginTransaction()
    .replace<MyFragment>(
        containerViewId = R.id.container,
        args = bundleOf("key" to "value"),
        tag = "my_fragment"
    )
    .commit()
```

## Fragment Factory with Scopes

To use Activity-scoped dependencies in your Fragment:

```kotlin
val appModule = module {
    scope<MyActivity> {
        scoped { ActivityService() }
        fragment { MyFragment(get()) }
    }
}
```

Pass the scope to `setupKoinFragmentFactory()`:

```kotlin
class MyActivity : AppCompatActivity(), AndroidScopeComponent {

    override val scope: Scope by activityScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Pass scope to fragment factory
        setupKoinFragmentFactory(scope)

        super.onCreate(savedInstanceState)
    }
}
```

## Quick Reference

| Action | Code |
|--------|------|
| Declare fragment | `fragment { MyFragment(get()) }` |
| Setup global factory | `setupKoinFragmentFactory()` |
| Setup with scope | `setupKoinFragmentFactory(scope)` |
| Add fragment | `.replace<MyFragment>(R.id.container)` |

## Next Steps

- **[AndroidX Fragment](https://developer.android.com/guide/fragments)** - Official Fragment documentation
- **[Scopes](/docs/reference/koin-android/scope)** - Android scopes
- **[ViewModel](/docs/reference/koin-android/viewmodel)** - ViewModel injection
