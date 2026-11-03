package org.koin.sample

import androidx.activity.ComponentActivity
import com.app.core.domain.ApiService
import com.app.core.domain.AuthService
import com.app.core.domain.User
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

class MainActivity : ComponentActivity()

@Singleton(binds = [ApiService::class])
class FakeApiService : ApiService { override suspend fun currentUser() = User() }

@Module
class ProvidedModule {
    @Singleton
    fun authService() = AuthService()
}

@Module(includes = [ProvidedModule::class])
@ComponentScan("org.koin.sample")
@Configuration
class FixtureModule
