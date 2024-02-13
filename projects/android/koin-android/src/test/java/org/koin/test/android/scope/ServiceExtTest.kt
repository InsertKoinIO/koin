package org.koin.test.android.scope

import android.app.Service
import android.content.Intent
import android.os.IBinder
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.koin.android.ext.android.getKoin
import org.koin.android.scope.AndroidScopeComponent
import org.koin.android.scope.createScope
import org.koin.android.scope.createServiceScope
import org.koin.android.scope.destroyServiceScope
import org.koin.core.KoinApplication
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.scope.Scope
import org.koin.test.KoinTest

@OptIn(KoinInternalApi::class)
class ServiceExtTest : KoinTest {

    private val componentCallbackExt = "org.koin.android.ext.android.ComponentCallbackExtKt"
    private lateinit var app: KoinApplication

    @Before
    fun before() {
        app = KoinApplication.init()
        startKoin(app)
        mockkStatic(componentCallbackExt)
    }

    @After
    fun after() {
        unmockkStatic(componentCallbackExt)
        stopKoin()
    }

    @Test
    fun `GIVEN wrong service type WHEN create service scope THEN fail`() {
        // GIVEN
        val service = mockk<Service>(relaxed = true)

        // WHEN
        try {
            service.createServiceScope()
        } catch (e: Exception) {
            Assert.assertEquals("Service should implement AndroidScopeComponent", e.message)
        }
    }

    @Test
    @Ignore
    fun `GIVEN service WHEN create service scope THEN set scope`() {
        // GIVEN
        val service = object : AndroidScopeComponent, Service() {
            override var scope: Scope = createScope("AndroidScopeComponent")
            override fun onBind(intent: Intent?): IBinder? = null
        }

        val previousScope = service.scope
        val expectedScope: Scope = mockk(relaxed = true)

        every { any<Service>().getKoin().getScopeOrNull(any()) } returns expectedScope

        // WHEN
        service.createServiceScope()

        Assert.assertTrue(previousScope != service.scope)
        Assert.assertTrue(expectedScope == service.scope)
    }

    @Test
    fun `GIVEN wrong service type WHEN destroy service scope THEN fail`() {
        // GIVEN
        val service = mockk<Service>(relaxed = true)

        // WHEN
        try {
            service.destroyServiceScope()
        } catch (e: Exception) {
            Assert.assertEquals("Service should implement AndroidScopeComponent", e.message)
        }
    }

    @Test
    @Ignore
    fun `GIVEN service WHEN destroy service scope THEN set scope as null`() {
        // GIVEN
        val service = object : AndroidScopeComponent, Service() {
            override var scope: Scope = createScope("AndroidScopeComponent")
            override fun onBind(intent: Intent?): IBinder? = null
        }

        val expectedScope: Scope = mockk(relaxed = true)
        every { any<Service>().getKoin().getScopeOrNull(any()) } returns expectedScope
        service.createServiceScope()

        // WHEN
        service.destroyServiceScope()

        verify(exactly = 1) { expectedScope.close() }
        Assert.assertNull(service.scope)
    }
}
