package org.koin.sample

import androidx.activity.ComponentActivity
import org.koin.dsl.module

// What the page leaves out: the shared types, and a profile module like the login one.
class User
interface ApiService { suspend fun currentUser(): User }
class FakeApiService : ApiService { override suspend fun currentUser() = User() }
class AuthService

val profileModule = module {
    single { AuthService() }
    single<ApiService> { FakeApiService() }
}

class MainActivity : ComponentActivity()
