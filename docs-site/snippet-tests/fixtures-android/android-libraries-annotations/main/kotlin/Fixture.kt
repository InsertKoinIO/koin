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

@org.koin.core.annotation.Module(includes = [AnalyticsModule::class])
@org.koin.core.annotation.Configuration
class FixtureModule

@org.koin.core.annotation.Singleton
class FakeTokenStore : TokenStore { override fun token() = "secret" }
