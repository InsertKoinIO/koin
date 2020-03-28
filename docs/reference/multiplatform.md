[![Tweet](https://img.shields.io/twitter/url/http/shields.io.svg?style=social)](https://twitter.com/insertkoin_io)
<a class="github-button" href="https://github.com/InsertKoinIO/koin" data-icon="octicon-star" data-show-count="true" aria-label="Star jhildenbiddle/docsify-themeable on GitHub">Star</a>

## Multiplatform Support

Kotlin Multiplatform allows you to write and compile code for a number of computing platforms. Broadly speaking, 
KMP supports the JVM, which is what most Kotlin runs on, as well as JS and Native. Native supports a wide range
of target devices including iOS, Mac, Windows, and various flavors of linux.

All of these targets have different runtime restrictions and capabilities. To some degree, support for reflection presents
an issue with regards to DI, but by far the biggest difference will be the state and concurrency model.

Javascript does not allow multiple threads, at least not in the same addressible memory space. As a result, there are no
concurrency issues.

Native's state and threading model is different than the JVM. You can have concurrent state access, but there are restrictions.

>  For more background info, see [Practical Kotlin Native Concurrency](https://dev.to/touchlab/practical-kotlin-native-concurrency-ac7).

To accomodate Native's threading model, all interaction with Koin is done on the main thread. In general for native mobile, dependency injection happens on the main thread, so this model makes conceptual sense. Attempting to interact with Koin from another thread will throw an exception.

Keeping state in the main thread allows us to keep instances mutable. If you define a `single`, you can freeze it, but Koin won't freeze it automatically, and if you never freeze it yourself, it will remain mutable.

### Android

The JVM, by default, will function as Koin has always functioned. You'll be able to access Koin from any thread. However, for a number of reasons, this is not always desireable.

You can enable main-thread-only access in Koin for android by including the `koin-android-main-thread` module in your Android config.

```groovy
implementation "org.koin:koin-android-main-thread:$koin_version"
```

On start, Koin will find the threading implementation and enforce the same rules in Android as they are enforced in native.