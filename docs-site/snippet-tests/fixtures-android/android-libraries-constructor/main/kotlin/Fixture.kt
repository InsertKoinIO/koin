package org.koin.sample

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey

// What the page leaves out: the classes the recipes declare.
@Entity
data class User(@PrimaryKey val id: String)

@Dao
interface UserDao

interface UserRepository {
    suspend fun getUser(userId: String): User = User(userId)
}

interface Analytics
class FirebaseAnalyticsTracker(val context: Context) : Analytics
class NoOpAnalytics : Analytics

class MainActivity : ComponentActivity()

class UserRemoteDataSource(private val api: ApiService)
class UserLocalDataSource(private val dao: UserDao)
class UserRepositoryImpl(
    private val remote: UserRemoteDataSource,
    private val local: UserLocalDataSource,
) : UserRepository
class GetUserUseCase(private val repository: UserRepository)
class UserViewModel(private val getUser: GetUserUseCase, private val handle: SavedStateHandle) : ViewModel()

class FakeTokenStore : TokenStore { override fun token() = "secret" }
val tokenModule = org.koin.dsl.module { single<TokenStore> { FakeTokenStore() } }
