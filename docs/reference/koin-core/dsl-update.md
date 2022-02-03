---
title: Koin Reflection DSL
---

## Compact definition declaration - no more "get()"

Koin DSL can be seen as "manual", while you must fill constructors with "get()" function to resolve needed instances. When your definition don't need any special constructor integration (injection parameters or special scope Id), we can go with more compact writing style thanks to API below.

:::note
 Using reflection is not costless, even if here it's really minimal. it replaces what you don"t want to write with reflection code (finding primary constructors, injecting parameters...). Mind it before using it, if you are on performances constraints platform (Android for example)
:::

Just use the single function without any expression:

```kotlin
module {
    single<ComponentA>() // will be equivalent to single { ComponentA() }
}
```

If you have an implementation type and want to resolve with a target type, you can use the regular `bind` or `binds` function:

```kotlin
module {
    single<Implementation>() bind Target::class
}
```

:::note
 This way of writing will detect your primary constructor. If you need to to detect default values, please use `@JvmOverLoad` on your constructor.
:::
