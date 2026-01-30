---
title: Android Library Integration
---

This guide covers integrating common Android libraries with Koin.

:::info
For core definition types and external library binding patterns, see [Definitions](/docs/reference/koin-core/definitions). This page focuses on Android-specific library examples.
:::

## Retrofit

```kotlin
interface ApiService {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: String): User
}

// Builder functions - Koin resolves parameters automatically
fun createOkHttpClient(): OkHttpClient =
    OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

fun createRetrofit(client: OkHttpClient): Retrofit =
    Retrofit.Builder()
        .baseUrl("https://api.example.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

fun createApiService(retrofit: Retrofit): ApiService =
    retrofit.create(ApiService::class.java)

val networkModule = module {
    single { create(::createOkHttpClient) }
    single { create(::createRetrofit) }
    single { create(::createApiService) }
}
```

Or with Annotations:

```kotlin
@Module
class NetworkModule {
    @Single
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()

    @Single
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.example.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Single
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}
```

## Room Database

```kotlin
@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

fun createDatabase(context: Context): AppDatabase =
    Room.databaseBuilder(context, AppDatabase::class.java, "app-database").build()

fun createUserDao(database: AppDatabase): UserDao = database.userDao()

val databaseModule = module {
    single { create(::createDatabase) }
    single { create(::createUserDao) }
}
```

Or with Annotations:

```kotlin
@Module
class DatabaseModule {
    @Single
    fun provideDatabase(context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "app-database").build()

    @Single
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()
}
```

## Gson / Serialization

```kotlin
fun createGson(): Gson =
    GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .setPrettyPrinting()
        .create()

val serializationModule = module {
    single { create(::createGson) }
}
```

## WorkManager

```kotlin
class SyncWorker(
    context: Context,
    params: WorkerParameters,
    private val repository: SyncRepository
) : Worker(context, params) {

    override fun doWork(): Result {
        repository.sync()
        return Result.success()
    }
}

val workModule = module {
    workerOf(::SyncWorker)
}
```

:::note
For WorkManager setup, use the `koin-androidx-workmanager` dependency. See [WorkManager Integration](/docs/reference/koin-android/workmanager) for complete setup.
:::

## Android Clean Architecture

### Repository Pattern

```kotlin
@Singleton
class UserRemoteDataSource(private val api: ApiService)

@Singleton
class UserLocalDataSource(private val database: AppDatabase)

@Singleton
class UserRepositoryImpl(
    private val remoteDataSource: UserRemoteDataSource,
    private val localDataSource: UserLocalDataSource
) : UserRepository

// DSL
val dataModule = module {
    single<UserRemoteDataSource>()
    single<UserLocalDataSource>()
    single<UserRepositoryImpl>() bind UserRepository::class
}
```

### Use Case Pattern

```kotlin
@Factory
class GetUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: String): Result<User> = runCatching {
        userRepository.getUser(userId)
    }
}

val domainModule = module {
    factory<GetUserUseCase>()
}
```

### ViewModel with SavedStateHandle

```kotlin
@KoinViewModel
class UserViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: String = savedStateHandle["userId"] ?: ""

    fun loadUser() {
        viewModelScope.launch {
            getUserUseCase(userId)
        }
    }
}

val viewModelModule = module {
    viewModel<UserViewModel>()
}
```

## Next Steps

- **[Definitions](/docs/reference/koin-core/definitions)** - Core definition types
- **[WorkManager](/docs/reference/koin-android/workmanager)** - Complete WorkManager setup
- **[Android Scopes](/docs/reference/koin-android/scope)** - Lifecycle-scoped instances
- **[ViewModel](/docs/reference/koin-android/viewmodel)** - ViewModel injection patterns
