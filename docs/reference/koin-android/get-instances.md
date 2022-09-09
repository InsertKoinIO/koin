---
title: Injecting in Android
---

Once you have declared some modules and you have started Koin, how can you retrieve your instances in your
Android Activity Fragments or Services?

## Activity, Fragment & Service as KoinComponents

Activity, Fragment & Service are extended with the KoinComponents extension. You gain access to:

* `by inject()` - lazy evaluated instance from Koin container
* `get()` - eager fetch instance from Koin container

For a module that declares a 'presenter' component:

```kotlin
val androidModule = module {
    // a factory of Presenter
    factory { Presenter() }
}
```

We can declare a property as lazy injected:

```kotlin
class DetailActivity : AppCompatActivity() {

    // Lazy injected Presenter instance
    override val presenter : Presenter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ...
    }
```

Or we can just directly get an instance:

```kotlin
class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve a Presenter instance
        val presenter : Presenter = get()
    }
```

### Need inject() and get() anywhere else?

If you need to `inject()` or `get()` an instance from another class, just tag it with `KoinComponent` interface.


