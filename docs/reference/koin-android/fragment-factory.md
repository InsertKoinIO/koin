---
title: Fragment Factory
description: Create Fragments with constructor injection through the AndroidX FragmentFactory and the Koin fragment definition.
---

By default, Android creates Fragments with their empty constructor, so you inject them with `by inject()` (see [Android Entry Points](/docs/reference/koin-android/entry-points)). With the AndroidX [FragmentFactory](https://developer.android.com/reference/kotlin/androidx/fragment/app/FragmentFactory), Koin creates the Fragment itself and passes dependencies to its constructor. This page covers the setup, the `fragment` definition and Fragments from an Activity scope.

The `fragment` definition is part of `io.insert-koin:koin-android`. No other artifact is needed.

## Setup

Enable the Koin fragment factory in `startKoin`:

```kotlin
import org.koin.androidx.fragment.koin.fragmentFactory

startKoin {
    androidContext(this@MainApplication)
    fragmentFactory()
    modules(appModule)
}
```

Then install it in each Activity that hosts these Fragments, before `super.onCreate()`:

```kotlin
import org.koin.androidx.fragment.android.setupKoinFragmentFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setupKoinFragmentFactory()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
```

`super.onCreate()` restores Fragments after a rotation or process death. The factory must be in place before that, or Android uses the default factory and fails on Fragments without an empty constructor.

## Declaring a Fragment

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

The Compiler Plugin DSL has no `fragment<T>()` function. Use the Classic DSL `fragment { }` or the Constructor DSL `fragmentOf()` in the same module:

```kotlin
class UserFragment(private val repository: UserRepository) : Fragment()

val uiModule = module {
    single<UserRepository>()
    fragmentOf(::UserFragment)
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

There is no Fragment annotation. Declare the Fragment in a DSL module, next to your annotated classes (see [DSL Reference](/docs/reference/koin-core/dsl)):

```kotlin
val uiModule = module {
    fragmentOf(::UserFragment)
}
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.fragment.dsl.fragmentOf

class UserFragment(private val repository: UserRepository) : Fragment()

val uiModule = module {
    singleOf(::UserRepository)
    fragmentOf(::UserFragment)

    // or write the constructor call
    fragment { UserFragment(get()) }
}
```

</TabItem>
</Tabs>

A `fragment` definition is a `factory`: every transaction gets a new Fragment instance. If no definition matches a Fragment class, the Koin factory falls back to the default AndroidX behavior.

## Adding a Fragment

Use the reified `replace` extension on `FragmentTransaction`:

```kotlin
import org.koin.androidx.fragment.android.replace

supportFragmentManager.beginTransaction()
    .replace<UserFragment>(R.id.container)
    .commit()
```

With arguments and a tag:

```kotlin
supportFragmentManager.beginTransaction()
    .replace<UserFragment>(
        containerViewId = R.id.container,
        args = bundleOf("USER_ID" to "42"),
        tag = "user",
    )
    .commit()
```

Pass per-instance values in `args`, not in the constructor: Android recreates the Fragment through the factory, and only the arguments are restored.

## Fragments from an Activity scope

To give a Fragment instances from its Activity scope, declare the Fragment inside the scope and pass the scope to `setupKoinFragmentFactory()`:

```kotlin
val checkoutModule = module {
    scope<CheckoutActivity> {
        scopedOf(::CheckoutSession)
        fragmentOf(::PaymentFragment)
    }
}
```

```kotlin
class CheckoutActivity : ScopeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setupKoinFragmentFactory(scope)
        super.onCreate(savedInstanceState)
    }
}
```

With a scope, the factory resolves Fragments from that scope, then from the root scope. In this case it does not need `fragmentFactory()` in `startKoin`: that option only declares the global factory used by `setupKoinFragmentFactory()` without arguments. Activity scopes are covered in [Android Scopes](/docs/reference/koin-android/scope).

## Related

- [Android Entry Points](/docs/reference/koin-android/entry-points): inject in a Fragment with `by inject()`.
- [Android Scopes](/docs/reference/koin-android/scope): Activity and Fragment scopes.
- [Definitions](/docs/reference/koin-core/definitions): `factory` and the other definition types.
- [AndroidX Fragments guide](https://developer.android.com/guide/fragments): the Fragment API.
