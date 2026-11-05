package org.koin.sample

// What the page leaves out: the other shared types, and implementations for the app to run.
class User
interface ApiService { suspend fun currentUser(): User }
interface AuthService
class FakeApiService : ApiService { override suspend fun currentUser() = User() }
class FakeAuthService : AuthService
