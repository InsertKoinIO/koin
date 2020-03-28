
Since AndroidX has released `androidx.fragment` packages family to extend features around Android `Fragment`

https://developer.android.com/jetpack/androidx/releases/fragment

## Fragment Factory

Since `2.1.0-alpha-3` version, has been introduce the `FragmentFactory`, a class dedicated to create instance of `Fragment` class:

https://developer.android.com/reference/kotlin/androidx/fragment/app/FragmentFactory

Koin can bring a `KoinFragmentFactory` to help you inject your `Fragment` instances directly.

## Setup Fragment Factory

At start, in your KoinApplication declaration, use the `fragmentFactory()` keyword to setup a default `KoinFragmentFactory` instance:

```kotlin
 startKoin {
    // setup a KoinFragmentFactory instance
    fragmentFactory()

    modules(...)
}
```

## Declare & Inject your Fragment

To declare a `Fragment` instance, just declare it as a `fragment` in your Koin module and use *constructor injection*.

Given a `Fragment` class:

```kotlin
class MyFragment(val myService: MyService) : Fragment() {


}
```

```kotlin
val appModule = module {
    single { MyService() }
    fragment { MyFragment(get()) }
}
```

## Get your Fragment

From your host `Activity` class, setup your fragment factory with `setupKoinFragmentFactory()`:

```kotlin
class MyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Koin Fragment Factory
        setupKoinFragmentFactory()

        super.onCreate(savedInstanceState)
        //...
    }
}
```

And retrieve your `Fragment` with your `supportFragmentManager`:

```kotlin
supportFragmentManager.beginTransaction()
            .replace(R.id.mvvm_frame, MyFragment::class.java, null, null)
            .commit()
```


## Fragment Factory & Koin Scopes

If you want to use the Koin Activity's Scope, you have to declare your fragment inside your scope as a `scoped` definition:

```kotlin
val appModule = module {
    scope<MyActivity> {
        fragment { MyFragment(get()) }
    }
}
```

and setup your Koin Fragment Factory with your scope: `setupKoinFragmentFactory(lifecycleScope)`

```kotlin
class MyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Koin Fragment Factory
        setupKoinFragmentFactory(lifecycleScope)

        super.onCreate(savedInstanceState)
        //...
    }
}
```

