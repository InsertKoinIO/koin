---
title: Advanced Patterns
---

# Advanced Patterns

This guide covers advanced dependency injection patterns for complex scenarios.

## External Library Binding

For third-party libraries where you can't add annotations, use builder functions with `create()`:

```kotlin
// Builder functions - Koin resolves parameters automatically
fun createOkHttpClient(): OkHttpClient =
    OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

fun createRetrofit(client: OkHttpClient): Retrofit =
    Retrofit.Builder()
        .baseUrl("https://api.example.com/")
        .client(client)
        .build()

fun createApiService(retrofit: Retrofit): ApiService =
    retrofit.create(ApiService::class.java)

val networkModule = module {
    single { create(::createOkHttpClient) }
    single { create(::createRetrofit) }
    single { create(::createApiService) }
}
```

Or with Annotations using `@Module` functions:

```kotlin
@Module
class NetworkModule {
    @Single
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).build()

    @Single
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder().baseUrl("https://api.example.com/").client(client).build()

    @Single
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}
```

## Collections of Dependencies

### Multiple Implementations

Aggregate multiple implementations of an interface using qualifiers:

```kotlin
interface PaymentProcessor {
    fun process(amount: Double): Boolean
    fun getName(): String
}

class CreditCardProcessor : PaymentProcessor { ... }
class PayPalProcessor : PaymentProcessor { ... }
class CryptoProcessor : PaymentProcessor { ... }
```

#### Compiler Plugin DSL

Use `@Named` qualifier annotations on classes:

```kotlin
@Named("creditCard")
class CreditCardProcessor : PaymentProcessor { ... }

@Named("paypal")
class PayPalProcessor : PaymentProcessor { ... }

@Named("crypto")
class CryptoProcessor : PaymentProcessor { ... }

class PaymentManager(
    @Named("creditCard") creditCard: PaymentProcessor,
    @Named("paypal") paypal: PaymentProcessor,
    @Named("crypto") crypto: PaymentProcessor
) {
    private val processors = listOf(creditCard, paypal, crypto)
}
```

```kotlin
val paymentModule = module {
    single<CreditCardProcessor>()
    single<PayPalProcessor>()
    single<CryptoProcessor>()
    single<PaymentManager>()
}
```

#### Annotations

```kotlin
@Module
class PaymentModule {
    @Single
    @Named("creditCard")
    fun provideCreditCard(): PaymentProcessor = CreditCardProcessor()

    @Single
    @Named("paypal")
    fun providePayPal(): PaymentProcessor = PayPalProcessor()

    @Single
    @Named("crypto")
    fun provideCrypto(): PaymentProcessor = CryptoProcessor()

    @Single
    fun providePaymentManager(
        @Named("creditCard") creditCard: PaymentProcessor,
        @Named("paypal") paypal: PaymentProcessor,
        @Named("crypto") crypto: PaymentProcessor
    ): PaymentManager = PaymentManager(listOf(creditCard, paypal, crypto))
}
```

#### Classic DSL

```kotlin
val paymentModule = module {
    single(named("creditCard")) { CreditCardProcessor() }
    single(named("paypal")) { PayPalProcessor() }
    single(named("crypto")) { CryptoProcessor() }

    single {
        PaymentManager(
            listOf(
                get(named("creditCard")),
                get(named("paypal")),
                get(named("crypto"))
            )
        )
    }
}
```

## Generic Types

Koin preserves generic type information:

```kotlin
interface Repository<T> {
    suspend fun get(id: String): T
    suspend fun save(item: T)
}

@Singleton
class UserRepository : Repository<User> { ... }

@Singleton
class ProductRepository : Repository<Product> { ... }
```

```kotlin
// DSL
val repositoryModule = module {
    single<Repository<User>> { UserRepository() }
    single<Repository<Product>> { ProductRepository() }
}

// Injection - types are distinct
val userRepo: Repository<User> = get()
val productRepo: Repository<Product> = get()
```

## Provider Pattern

Create factories for runtime instances when you need to create objects with runtime parameters:

```kotlin
@Factory
class DialogFactory(private val context: Context) {
    fun createConfirmDialog(title: String, onConfirm: () -> Unit): AlertDialog =
        AlertDialog.Builder(context)
            .setTitle(title)
            .setPositiveButton("OK") { _, _ -> onConfirm() }
            .create()

    fun createErrorDialog(message: String): AlertDialog =
        AlertDialog.Builder(context)
            .setTitle("Error")
            .setMessage(message)
            .create()
}

// Usage
class MyScreen(private val dialogFactory: DialogFactory) {
    fun showConfirmation() {
        dialogFactory.createConfirmDialog("Confirm") { /* action */ }.show()
    }
}
```

## Decorator Pattern

Stack behaviors using delegation:

```kotlin
interface NotificationService {
    fun send(message: String)
}

@Singleton
class BasicNotificationService : NotificationService {
    override fun send(message: String) { /* send */ }
}

class LoggingNotificationDecorator(
    private val delegate: NotificationService,
    private val logger: Logger
) : NotificationService {
    override fun send(message: String) {
        logger.log("Sending: $message")
        delegate.send(message)
    }
}

class RateLimitedNotificationDecorator(
    private val delegate: NotificationService,
    private val rateLimiter: RateLimiter
) : NotificationService {
    override fun send(message: String) {
        if (rateLimiter.tryAcquire()) delegate.send(message)
    }
}
```

Stack decorators in module:

```kotlin
fun createLogger(): Logger = ConsoleLogger()
fun createRateLimiter(): RateLimiter = TokenBucketRateLimiter()

val notificationModule = module {
    single { BasicNotificationService() }
    single { create(::createLogger) }
    single { create(::createRateLimiter) }

    single<NotificationService> {
        RateLimitedNotificationDecorator(
            delegate = LoggingNotificationDecorator(
                delegate = get<BasicNotificationService>(),
                logger = get()
            ),
            rateLimiter = get()
        )
    }
}
```

## Next Steps

- **[Definitions](/docs/reference/koin-core/definitions)** - Basic definition types
- **[Qualifiers](/docs/reference/koin-core/qualifiers)** - Named and typed qualifiers
- **[Troubleshooting](/docs/reference/koin-core/troubleshooting)** - Debug and fix issues
