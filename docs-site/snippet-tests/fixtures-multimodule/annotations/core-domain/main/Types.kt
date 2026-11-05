package com.app.core.domain

// What the page leaves out: the shared types.
class User
interface ApiService { suspend fun currentUser(): User }
interface AuthService
interface UserRepository { suspend fun currentUser(): User }
