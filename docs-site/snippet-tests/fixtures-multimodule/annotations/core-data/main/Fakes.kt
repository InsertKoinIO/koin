package com.app.core.data

import com.app.core.domain.ApiService
import com.app.core.domain.AuthService
import com.app.core.domain.User
import org.koin.core.annotation.Singleton

// The fakes stand in for the Retrofit and auth definitions, which the page leaves out.
@Singleton
internal class FakeApiService : ApiService { override suspend fun currentUser() = User() }

@Singleton
internal class FakeAuthService : AuthService
