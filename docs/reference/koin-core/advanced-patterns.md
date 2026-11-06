---
title: Advanced Patterns
description: Inject every implementation of an interface, declare generic types, create objects at runtime with a provider, and stack decorators with Koin.
---

This page covers patterns beyond one class per definition: collections of implementations, generic types, providers for runtime objects, and decorators. It assumes you know [definitions](/docs/reference/koin-core/definitions) and [qualifiers](/docs/reference/koin-core/qualifiers). For classes from libraries (Retrofit, Room, OkHttp), see [Classes you don't own](/docs/reference/koin-core/definitions#classes-you-dont-own).

## Collections of dependencies

To inject every implementation of an interface, bind each one to the interface and ask for a `List`:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
interface PaymentProcessor {
    fun process(amount: Double): Boolean
}

class CreditCardProcessor : PaymentProcessor { ... }
class PayPalProcessor : PaymentProcessor { ... }

class PaymentManager(private val processors: List<PaymentProcessor>)

val paymentModule = module {
    single<CreditCardProcessor>() bind PaymentProcessor::class
    single<PayPalProcessor>() bind PaymentProcessor::class
    single<PaymentManager>()
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
interface PaymentProcessor {
    fun process(amount: Double): Boolean
}

// bound to PaymentProcessor automatically
@Singleton
class CreditCardProcessor : PaymentProcessor { ... }

@Singleton
class PayPalProcessor : PaymentProcessor { ... }

@Singleton
class PaymentManager(private val processors: List<PaymentProcessor>)
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val paymentModule = module {
    singleOf(::CreditCardProcessor) bind PaymentProcessor::class
    singleOf(::PayPalProcessor) bind PaymentProcessor::class
    single { PaymentManager(getAll()) }
}
```

</TabItem>
</Tabs>

A `List<T>` parameter resolves with `getAll<T>()`: every definition whose type, or one of its bound types, is `T`, with or without a qualifier. Outside a constructor, call `getAll<PaymentProcessor>()` on a `Scope` or on `getKoin()`.

Behavior to know:

- **Empty list**: if nothing is bound to `T`, the list is empty. No error, and the Compiler Plugin does not report a missing dependency for a `List<T>` parameter.
- **No order**: do not rely on the order of the list. If order matters, sort it, for example by a `priority` property on the interface.
- **Current scope only**: from a scope instance, `getAll` returns the definitions of that scope and of its linked scopes, including the root.
- **Single lookup**: `get<PaymentProcessor>()` still returns only one of the bound definitions (the last one loaded). To pick one, give each a [qualifier](/docs/reference/koin-core/qualifiers).

- **Strict override**: with `allowOverride(false)`, binding the same interface without a qualifier in two different modules fails at start with `DefinitionOverrideException`. Give each binding a qualifier.

When you need both the list and a specific implementation, combine binding and qualifiers. With the Compiler Plugin DSL and annotations, put `@Named` on the class:

```kotlin
@Named("creditCard")
class CreditCardProcessor : PaymentProcessor { ... }

@Named("paypal")
class PayPalProcessor : PaymentProcessor { ... }

val paymentModule = module {
    single<CreditCardProcessor>() bind PaymentProcessor::class
    single<PayPalProcessor>() bind PaymentProcessor::class
}

class CheckoutService(
    @Named("creditCard") private val defaultProcessor: PaymentProcessor,
    private val allProcessors: List<PaymentProcessor>,
)
```

## Generic types

Koin identifies a definition by its class and qualifier. Type arguments are erased: `Repository<User>` and `Repository<Product>` are the same key, `Repository`. Two definitions of the same generic class override each other, and the Compiler Plugin validates them as one.

```kotlin
// Does not work: the second definition overrides the first
val repositoryModule = module {
    single<Repository<User>> { UserRepository() }
    single<Repository<Product>> { ProductRepository() }
}
```

Use one of these instead:

- **Concrete types**: inject `UserRepository` and `ProductRepository` directly. Each class is its own key.
- **Qualifiers**: keep the generic interface and tell the definitions apart with a qualifier.

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
@Qualifier(User::class)
class UserRepository : Repository<User> { ... }

@Qualifier(Product::class)
class ProductRepository : Repository<Product> { ... }

val repositoryModule = module {
    single<UserRepository>() bind Repository::class
    single<ProductRepository>() bind Repository::class
}

class UserService(@Qualifier(User::class) private val repository: Repository<User>)
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Singleton
@Qualifier(User::class)
class UserRepository : Repository<User> { ... }

@Singleton
@Qualifier(Product::class)
class ProductRepository : Repository<Product> { ... }

@Singleton
class UserService(@Qualifier(User::class) private val repository: Repository<User>)
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val repositoryModule = module {
    single<Repository<User>>(named<User>()) { UserRepository() }
    single<Repository<Product>>(named<Product>()) { ProductRepository() }
    single { UserService(get(named<User>())) }
}
```

</TabItem>
</Tabs>

`named<User>()` is a type qualifier built from the class `User`. `@Qualifier(User::class)` is its annotation form.

## Provider pattern

Some objects need values that exist only at runtime: a title, a callback, a user choice. Instead of resolving them from Koin each time, inject a provider class that creates them:

```kotlin
class DialogProvider(private val context: Context) {

    fun confirmDialog(title: String, onConfirm: () -> Unit): AlertDialog =
        AlertDialog.Builder(context)
            .setTitle(title)
            .setPositiveButton("OK") { _, _ -> onConfirm() }
            .create()
}

class SettingsScreen(private val dialogs: DialogProvider) {
    fun askReset() {
        dialogs.confirmDialog("Reset settings?") { reset() }.show()
    }
}
```

Declare `DialogProvider` like any class (`factory<DialogProvider>()`, `@Factory`, or `factoryOf(::DialogProvider)`). The provider gets its dependencies from Koin, the created objects get their runtime values from the caller.

For a value known at the call site and needed in the constructor, [injected parameters](/docs/reference/koin-core/injection-parameters) are the simpler option. For an instance that is costly and not always needed, inject `Lazy<T>`: see [Constructor parameters](/docs/reference/koin-core/definitions#constructor-parameters).

## Decorator pattern

A decorator wraps an implementation to add behavior (logging, caching, rate limiting) and exposes the same interface. Koin has no decorator keyword: register the inner implementation under its own type, and bind the interface to the outer decorator.

```kotlin
interface NotificationService {
    fun send(message: String)
}

class PushNotificationService : NotificationService { ... }

class LoggingNotificationService(
    private val delegate: NotificationService,
    private val logger: Logger,
) : NotificationService {
    override fun send(message: String) {
        logger.log("Sending: $message")
        delegate.send(message)
    }
}
```

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
import org.koin.plugin.module.dsl.create

fun loggingNotifications(
    push: PushNotificationService,
    logger: Logger,
): NotificationService = LoggingNotificationService(push, logger)

val notificationModule = module {
    single<Logger>()
    single<PushNotificationService>()
    single { create(::loggingNotifications) }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Module
class NotificationModule {

    @Singleton
    fun notifications(push: PushNotificationService, logger: Logger): NotificationService =
        LoggingNotificationService(push, logger)
}
```

Declare `PushNotificationService` with `@Singleton(binds = [])`, so it is not bound to `NotificationService` as well.

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val notificationModule = module {
    singleOf(::Logger)
    singleOf(::PushNotificationService)
    single<NotificationService> {
        LoggingNotificationService(delegate = get<PushNotificationService>(), logger = get())
    }
}
```

</TabItem>
</Tabs>

Consumers ask for `NotificationService` and get the decorated instance. To stack several decorators, nest them in the same function: `RateLimitedNotificationService(LoggingNotificationService(push, logger), limiter)`.

The inner implementation must not be bound to the interface too. Otherwise two definitions answer for `NotificationService` and one overrides the other.

## Related

- [Definitions](/docs/reference/koin-core/definitions): definition types, binding, classes you don't own.
- [Qualifiers](/docs/reference/koin-core/qualifiers): `named()`, type qualifiers and `@Named`.
- [Injected Parameters](/docs/reference/koin-core/injection-parameters): runtime values passed at resolution.
- [Injection](/docs/reference/koin-core/injection): `get()`, `getAll()`, `inject()` outside constructors.
- [Troubleshooting](/docs/reference/koin-core/troubleshooting): override and missing definition errors.
