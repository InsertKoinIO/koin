

## Starting a Koin module from Java

A Koin module still must be written in Kotlin, using the `@JvmField` to make it Java friendly:

```kotlin
@JvmField
val koinModule = module {

    single { ComponentA() }
}
```

Now just call the `KoinJavaStarter.startKoin` function from a Java class to start your module:

[source,java]
```
KoinJavaStarter.startKoin(singletonList(koinModule));
```


## Injecting into a Java class

The `koin-java` project provides a static Java helper: `KoinJavaComponent`. This will help you make:


* direct instance injection with `get(Class)`

[source,java]
```
ComponentA a = get(ComponentA.class);
```

* lazy instance injection with `inject(Class)`

[source,java]
```
Lazy<ComponentA> lazy_a = inject(ComponentA.class);
```

?> Make sure to have the static import of class `KoinJavaComponent` to have a short syntax.







