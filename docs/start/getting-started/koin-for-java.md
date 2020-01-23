
Below is a short description of `koin-java` features. This project is a small subset of static utils to help java developers.

## Start Koin

Just use the `startKoin()` static function (with the static import to reduce the syntax):

```java
import static org.koin.core.context.GlobalContext.start;

// Build KoinApplication instance
// Builder API style
KoinApplication koinApp = KoinApplication.create()
                .printLogger()
                .modules(koinModule);

// Statr KoinApplication instance
start(koinApp);
```

You have access to the [same options]({{ site.baseurl }}/docs/{{ site.docs_version }}/quick-references/koin-core/#start-koin) than the Kotlin `startKoin()`.

## Java friendly module in Kotlin

You need to tag your module variable with `@JvmField` to make it readable from Java world:

```kotlin
@JvmField
val koinModule = module {
    single { ComponentA() }
    single { ComponentB(get()) }
    single { ComponentC(get(), get()) }

    module("anotherModule") {
        single { ComponentD(get()) }
    }
}
```

## Inject with static functions

The `KoinJavaComponent` class is a static helper that brings Koin powers to Java:

* `inject()` - lazy inject instance
* `get()` - retrieve instance
* `getKoin()` - get Koin context

```java
import static org.koin.java.standalone.KoinJavaComponent.*;

ComponentA a = get(ComponentA.class);
Lazy<ComponentA> lazy_a = inject(ComponentA.class);
```
