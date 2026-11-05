package org.koin.sample

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.qualifier.named
import org.koin.mp.KoinPlatform
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Retrofit

// Starts the real Application. Room is compiled but not opened, because the sample has no Room compiler.
// A single test: Robolectric restarts the Application for each one, and with it Koin.
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class LibrariesTest {
    @Test
    fun theRecipesResolveAndWork() {
        val koin = KoinPlatform.getKoin()
        koin.get<Retrofit>()
        koin.get<ApiService>()
        assertTrue(koin.get<Analytics>() is NoOpAnalytics)

        // The two clients are distinct, and only the qualified one sends the token.
        val public = koin.get<OkHttpClient>()
        val authenticated = koin.get<OkHttpClient>(named("authenticated"))
        assertNotSame(public, authenticated)
        assertEquals("Bearer secret", authorizationSentBy(authenticated))
        assertEquals(null, authorizationSentBy(public))
        koin.get<AccountApi>()

        // Dispatchers.
        assertSame(Dispatchers.IO, koin.get<CoroutineDispatcher>(named("io")))
        runBlocking { koin.get<SyncRepository>().sync() }

        // DataStore writes and reads back.
        val dataStore = koin.get<DataStore<Preferences>>()
        val key = stringPreferencesKey("theme")
        runBlocking {
            dataStore.edit { it[key] = "dark" }
            assertEquals("dark", dataStore.data.first()[key])

            // What the page says about a second instance on the same file.
            val second = PreferenceDataStoreFactory.create {
                ApplicationProvider.getApplicationContext<Context>().preferencesDataStoreFile("settings")
            }
            assertTrue(runCatching { second.data.first() }.exceptionOrNull() is IllegalStateException)
        }
    }

    // Answers without any network, and reports the header the request would carry.
    private fun authorizationSentBy(client: OkHttpClient): String? {
        var header: String? = null
        client.newBuilder().addInterceptor { chain ->
            header = chain.request().header("Authorization")
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body("".toResponseBody())
                .build()
        }.build().newCall(Request.Builder().url("https://example.com/").build()).execute().close()
        return header
    }
}
