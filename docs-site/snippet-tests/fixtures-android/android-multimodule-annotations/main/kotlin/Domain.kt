package com.app.core.domain

// What the page leaves out: the shared types. ApiService and AuthService come from the data layer.
class User
interface ApiService { suspend fun currentUser(): User }
class AuthService
interface UserRepository { suspend fun currentUser(): User }
