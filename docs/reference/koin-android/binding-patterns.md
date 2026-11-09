---
title: Android Library Integration
description: Complete Koin setups for Retrofit with OkHttp, Room, JSON serialization and a layered Android architecture.
---

This page gives complete Koin setups for libraries most Android apps use: Retrofit with OkHttp, Room and a JSON serializer, then a layered app structure. Library classes come from builders, so they are declared with provider functions. How that works (`create(::function)`, annotated functions) is explained in [Definitions](/docs/reference/koin-core/definitions#classes-you-dont-own); this page only shows the Android setups.

## Retrofit and OkHttp

```kotlin
interface ApiService {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: String): User
}
```

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
import org.koin.plugin.module.dsl.create

fun okHttpClient(): OkHttpClient =
    OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        })
        .build()

fun retrofit(client: OkHttpClient, json: Json): Retrofit =
    Retrofit.Builder()
        .baseUrl(BuildConfig.API_URL)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

fun apiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

val networkModule = module {
    single { create(::okHttpClient) }
    single { create(::retrofit) }
    single { create(::apiService) }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Module
class NetworkModule {

    @Singleton
    fun okHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })
            .build()

    @Singleton
    fun retrofit(client: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Singleton
    fun apiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
}
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val networkModule = module {
    single {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })
            .build()
    }
    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(get())
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
    }
    single { get<Retrofit>().create(ApiService::class.java) }
}
```

</TabItem>
</Tabs>

Declare `OkHttpClient` and `Retrofit` as `single`: each one holds a connection pool and thread pools, and creating several wastes them. `BuildConfig.API_URL` is a `buildConfigField` you define per build type. The `Json` instance comes from the next section.

## JSON serialization

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
fun json(): Json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

val serializationModule = module {
    single { create(::json) }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Module
class SerializationModule {

    @Singleton
    fun json(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }
}
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val serializationModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }
}
```

</TabItem>
</Tabs>

The same pattern works for Gson (`single { create(::gson) }` with `fun gson(): Gson = GsonBuilder().create()`) or Moshi. One shared instance keeps the serializer configuration in one place.

## Room

```kotlin
@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
```

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
fun appDatabase(context: Context): AppDatabase =
    Room.databaseBuilder(context, AppDatabase::class.java, "app-database").build()

fun userDao(database: AppDatabase): UserDao = database.userDao()

val databaseModule = module {
    single { create(::appDatabase) }
    single { create(::userDao) }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Module
class DatabaseModule {

    @Singleton
    fun appDatabase(context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "app-database").build()

    @Singleton
    fun userDao(database: AppDatabase): UserDao = database.userDao()
}
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val databaseModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "app-database").build()
    }
    single { get<AppDatabase>().userDao() }
}
```

</TabItem>
</Tabs>

Declare the database as a `single`: a `RoomDatabase` instance is costly, and the Room documentation asks for one instance per database in a process. `Context` here is the `Application`, given by `androidContext()` at start (see [Starting Koin on Android](/docs/reference/koin-android/start)). DAOs are cheap and Room returns the same instance for each call, so `single` or `factory` both work.

## WorkManager

Workers get their dependencies through a dedicated `WorkerFactory`. Setup and the `worker` definition are in [WorkManager](/docs/reference/koin-android/workmanager).

## Layered architecture

A typical Android app has a data layer (network, database, repositories), a domain layer (use cases) and a UI layer (ViewModels). Each layer uses the definition type that matches its lifetime:

| Layer | Classes | Definition |
|-------|---------|------------|
| Data | `OkHttpClient`, `Retrofit`, `AppDatabase`, data sources, repositories | `single` |
| Domain | Use cases, with no state | `factory` |
| UI | ViewModels | `viewModel` |

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
val dataModule = module {
    includes(networkModule, databaseModule, serializationModule)
    single<UserRemoteDataSource>()
    single<UserLocalDataSource>()
    single<UserRepositoryImpl>() bind UserRepository::class
}

val domainModule = module {
    factory<GetUserUseCase>()
}

val uiModule = module {
    viewModel<UserViewModel>()
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Singleton
class UserRemoteDataSource(private val api: ApiService)

@Singleton
class UserLocalDataSource(private val dao: UserDao)

@Singleton
class UserRepositoryImpl(
    private val remote: UserRemoteDataSource,
    private val local: UserLocalDataSource,
) : UserRepository

@Factory
class GetUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(userId: String): User = repository.getUser(userId)
}

@KoinViewModel
class UserViewModel(
    private val getUser: GetUserUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel()
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val dataModule = module {
    includes(networkModule, databaseModule, serializationModule)
    singleOf(::UserRemoteDataSource)
    singleOf(::UserLocalDataSource)
    singleOf(::UserRepositoryImpl) bind UserRepository::class
}

val domainModule = module {
    factoryOf(::GetUserUseCase)
}

val uiModule = module {
    viewModelOf(::UserViewModel)
}
```

</TabItem>
</Tabs>

The ViewModel depends on the use case, the use case on the `UserRepository` interface, and only the data module knows `UserRepositoryImpl`. To swap the data source in tests, override the `UserRepository` binding (see [Unit Testing](/docs/reference/koin-test/testing)). When each layer lives in its own Gradle module, see [Multi-Module Apps](/docs/reference/koin-android/multi-module).

## Related

- [Definitions](/docs/reference/koin-core/definitions): definition types, interface binding and provider functions.
- [Starting Koin on Android](/docs/reference/koin-android/start): `androidContext()` and Android Context in definitions.
- [Android ViewModel](/docs/reference/koin-android/viewmodel): retrieve ViewModels in Activities and Fragments.
- [WorkManager](/docs/reference/koin-android/workmanager): constructor injection in Workers.
- [Multi-Module Apps](/docs/reference/koin-android/multi-module): one Koin module per Gradle module.
