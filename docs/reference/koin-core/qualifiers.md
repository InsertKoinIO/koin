---
title: Qualifiers
description: Tell apart several definitions of the same type with named(), type qualifiers, @Named and custom qualifier annotations.
---

A qualifier gives a definition a name, so you can declare several definitions of the same type and ask for a specific one. This page covers string, type and enum qualifiers, custom qualifier annotations, and common mistakes. It assumes you know how to declare a definition (see [Definitions](/docs/reference/koin-core/definitions)).

## When you need a qualifier

Koin finds a definition by its type. If two definitions provide the same type, for example two `Database` instances, a plain request for `Database` can't choose between them. A qualifier tells them apart.

If the two objects have different roles, consider giving them different types instead (`LocalDatabase` and `RemoteDatabase` as their own interfaces). A type mismatch is caught by the compiler, a wrong qualifier string is not.

## Named qualifiers

Declare each definition with a name, then ask for the name where you inject it:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
import org.koin.core.annotation.Named
import org.koin.core.qualifier.named
import org.koin.plugin.module.dsl.*

class UserRepository(
    @Named("local") private val localDb: Database,
    @Named("remote") private val remoteDb: Database,
)

val dataModule = module {
    single<Database>(named("local")) { create(::LocalDatabase) }
    single<Database>(named("remote")) { create(::RemoteDatabase) }
    single<UserRepository>()
}
```

`single<UserRepository>()` reads `@Named` on the constructor parameters and resolves the qualified definitions. You can also put `@Named("local")` on the `LocalDatabase` class itself: `single<LocalDatabase>()` then registers it with that name. The `@Named` annotation comes from the `koin-annotations` artifact.

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
import org.koin.core.annotation.Named
import org.koin.core.annotation.Singleton

@Singleton
@Named("local")
class LocalDatabase : Database

@Singleton
@Named("remote")
class RemoteDatabase : Database

@Singleton
class UserRepository(
    @Named("local") private val localDb: Database,
    @Named("remote") private val remoteDb: Database,
)
```

`@Named` works on classes, provider functions and parameters.

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
import org.koin.core.qualifier.named

val dataModule = module {
    single<Database>(named("local")) { LocalDatabase() }
    single<Database>(named("remote")) { RemoteDatabase() }
    single {
        UserRepository(
            localDb = get(named("local")),
            remoteDb = get(named("remote")),
        )
    }
}
```

`singleOf(::UserRepository)` resolves parameters by type only, so it can't pass the qualifiers. Write the constructor call with `get(named(...))`, as shown.

</TabItem>
</Tabs>

Outside of a definition, pass the qualifier to `get()` or `inject()`:

```kotlin
val localDb: Database by inject(named("local"))
val remoteDb: Database = get(named("remote"))
```

The same qualifier argument exists on every injection API, such as `koinInject(named("local"))` in Compose. See [Injection](/docs/reference/koin-core/injection).

## Type qualifiers

A type can serve as the qualifier. Typos become compile errors, and renaming the type in your IDE updates every use:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
import org.koin.core.annotation.Qualifier

object LocalDb
object RemoteDb

class UserRepository(
    @Qualifier(LocalDb::class) private val localDb: Database,
    @Qualifier(RemoteDb::class) private val remoteDb: Database,
)

val dataModule = module {
    single<Database>(named<LocalDb>()) { create(::LocalDatabase) }
    single<Database>(named<RemoteDb>()) { create(::RemoteDatabase) }
    single<UserRepository>()
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
object LocalDb
object RemoteDb

@Singleton
@Qualifier(LocalDb::class)
class LocalDatabase : Database

@Singleton
@Qualifier(RemoteDb::class)
class RemoteDatabase : Database

@Singleton
class UserRepository(
    @Qualifier(LocalDb::class) private val localDb: Database,
    @Qualifier(RemoteDb::class) private val remoteDb: Database,
)
```

`@Qualifier(name = "local")` is the string form, equivalent to `@Named("local")`.

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
object LocalDb
object RemoteDb

val dataModule = module {
    single<Database>(named<LocalDb>()) { LocalDatabase() }
    single<Database>(named<RemoteDb>()) { RemoteDatabase() }
    single { UserRepository(get(named<LocalDb>()), get(named<RemoteDb>())) }
}
```

</TabItem>
</Tabs>

At the call site: `get<Database>(named<LocalDb>())`. `qualifier<LocalDb>()` is an alias of `named<LocalDb>()`.

## Enum qualifiers

An enum entry can be a qualifier too:

```kotlin
enum class DbType { LOCAL, REMOTE }

val dataModule = module {
    single<Database>(named(DbType.LOCAL)) { LocalDatabase() }
    single<Database>(named(DbType.REMOTE)) { RemoteDatabase() }
}

val localDb: Database = get(named(DbType.LOCAL))
```

`named(DbType.LOCAL)` is a string qualifier made from the lowercase entry name. It is the same qualifier as `named("local")`.

## Custom qualifier annotations

With annotations, you can create your own qualifier annotation. Annotate it with `@Qualifier` (or `@Named`), then use it like `@Named`:

```kotlin
import org.koin.core.annotation.Qualifier

@Qualifier
annotation class BaseUrl

@Qualifier
annotation class AuthUrl

@Singleton
@BaseUrl
fun baseUrl(): String = "https://api.example.com"

@Singleton
@AuthUrl
fun authUrl(): String = "https://auth.example.com"

@Singleton
class ApiClient(
    @BaseUrl private val baseUrl: String,
    @AuthUrl private val authUrl: String,
)
```

An annotation with no parameter becomes a type qualifier on the annotation class. From code, retrieve it with `get<String>(named<BaseUrl>())`.

A custom qualifier can take a parameter, to define a family of qualifiers with one annotation:

```kotlin
enum class AppDispatchers { Default, IO }

@Qualifier
annotation class Dispatcher(val dispatcher: AppDispatchers)

@Module
class DispatchersModule {

    @Singleton
    @Dispatcher(AppDispatchers.IO)
    fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Singleton
    @Dispatcher(AppDispatchers.Default)
    fun defaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Singleton
    fun applicationScope(
        @Dispatcher(AppDispatchers.Default) dispatcher: CoroutineDispatcher,
    ): CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)
}
```

The parameter value becomes the qualifier:

- String parameter: `@ApiName("users")` is the string qualifier `"users"`, the same as `@Named("users")`.
- Enum parameter: `@Dispatcher(AppDispatchers.IO)` is the string qualifier made of the enum's full class name and the entry, `named("com.myapp.AppDispatchers.IO")`. It is not the same as `named(AppDispatchers.IO)`, which uses the lowercase entry name.

The Compiler Plugin checks custom qualifiers at build time: a provider and an injection point with different qualifiers is a build error. Custom qualifier annotations also work with the Compiler Plugin DSL, on constructor parameters and on the class passed to `single<T>()`.

Custom qualifier annotations are not available in the Constructor or Classic DSL. Use `named()` there.

## JSR-330 qualifiers

`jakarta.inject.Named`, `javax.inject.Named` and annotations marked with `jakarta.inject.Qualifier` work like their Koin equivalents. See [JSR-330 Support](/docs/reference/koin-android/jsr330).

## Common mistakes

- Missing qualifier at the injection point: `get<Database>()` fails with `NoDefinitionFoundException` when every `Database` definition has a qualifier. A definition with a qualifier is only found with that qualifier.
- Different spelling: `named("encrypted")` and `named("encrypted_client")` are two different qualifiers. Use type or enum qualifiers, or keep the strings in constants, to avoid this.
- Qualifiers on every definition: if the types are already different, a qualifier adds nothing. Use one only when two definitions share a type.

With the Compiler Plugin, the first two mistakes are build errors. When a qualified parameter has no match but an unqualified definition of the same type exists, the error message points to it.

## Related

- [Definitions](/docs/reference/koin-core/definitions): declare definitions and bind them to interfaces.
- [Injection](/docs/reference/koin-core/injection): pass a qualifier to `get()` and `inject()`.
- [Injected Parameters](/docs/reference/koin-core/injection-parameters): pass values at resolution time instead of declaring variants.
- [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety): what the Compiler Plugin checks about qualifiers.
- [Annotations Reference](/docs/reference/koin-annotations/annotations-inventory): parameters of `@Named` and `@Qualifier`.
